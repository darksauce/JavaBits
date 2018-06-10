/* Open Source Licensed under GNU LGPL 3.0
 * See http://www.gnu.org/copyleft/lesser.html for details. */
package com.magi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ExpiryHashMap is a HashMap that expires it's entries after a set time period.
 *
 * Very useful for implementing data caching that expires after a pre-set number
 * of milliseconds.
 *
 * NOTE: Like HashMap, this object is not thread safe. You need to synchronize
 * any multi-threaded access to this object.
 *
 * @author  patkins
 * @version Revision 1.1
 */
public class ExpiryHashMap implements Map
{
    private long    timeout = 600000; // default is 10 minutes
    private Long    lastCleaned = new Long(System.currentTimeMillis());
    private boolean autoTimeout = false;
    private Map     map = null;

    /**
     * Create the map with a default timeout period of 10 minutes.
     */
    public ExpiryHashMap()
    {
        this.map = new HashMap();
    }

    /**
     * Create the map with the specified timeout period in milliseconds.
     *
     * @param timeout timeout period.
     */
    public ExpiryHashMap(long timeout)
    {
        this.timeout = timeout;
        this.map = new HashMap();
    }

    /**
     * Create the map with the specified timeout period and initial map capacity.
     *
     * @param timeout timeout period.
     * @param initialCapacity the initial capacity of the map entries.
     */
    public ExpiryHashMap(long timeout, int initialCapacity)
    {
        this.timeout = timeout;
        this.map = new HashMap(initialCapacity);
    }

    /**
     * Implements Map.get(Object)
     *
     * Retrieve the value Object for this key.
     * If the key does not exist, or the entry has timed out null is returned.
     */
    public Object get(Object key)
    {
        if (autoTimeout)
            periodicTimeoutEntries();

        Object[] value = (Object[]) map.get(key);

        if (value == null) // no entry found
            return null;

        if ( isExpired(value[0]) )
        {
            // Map entry has timed out - remove it and return null
            map.remove(key);
            return null;
        }

        // Return the entry found - entry is still valid
        return value[1];
    }

    /**
     * Implements Map.put(Object, Object)
     *
     * Put a new timestamped key/value pair into the map.
     */
    public Object put(Object key, Object value)
    {
        // Insert the new key/value pair with a current timestamp
        Object[] obj = new Object[2];
        obj[0] = new Long(System.currentTimeMillis());
        obj[1] = value;

        obj = (Object[]) map.put(key, obj);

        if (obj != null)
        {
            if ( !isExpired(obj[0]) )
                return obj[1]; // previous entry still valid - return it
        }

        if (autoTimeout)
            periodicTimeoutEntries();

        return null;
    }

    /* implements Map.clear() */
    public void clear()
    {
        map.clear();
    }

    /**
     * Scan map and remove any timed out entries.
     * This is not necessary but is recommended for reducing memory usage.
     *
     * @see #periodicTimeoutEntries()
     */
    public void timeoutEntries()
    {
        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            Object key = it.next();
            Object[] obj = (Object[]) map.get(key);

            if (obj != null)
            {
                if (isExpired(obj[0]))
                {
                    it.remove();
                }
            }
        }
    }

    /**
     * Periodically scan map and remove any timed out entries.
     * This method will perform a call to timeoutEntries() every n milliseconds,
     * as set in the constructor.  This is not necessary but is recommended for
     * reducing memory usage.
     *
     * @see #timeoutEntries()
     */
    public void periodicTimeoutEntries()
    {
        if (isExpired( lastCleaned ))
        {
            timeoutEntries();

            lastCleaned = new Long(System.currentTimeMillis());
        }
    }

    /* Implements Map.containsKey(Object) */
    public boolean containsKey(Object key)
    {
        return (get(key) != null);
    }

    /**
     * Implements Map.containsValue(Object)
     *
     * Returns true if the value object exists in the map.
     * If the value does not exist, or the value entry has timed out, false is returned.
     */
    public boolean containsValue(Object value)
    {
        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            Object key = it.next();
            Object[] obj = (Object[]) map.get(key);
            if ( !isExpired(obj[0]) )
            {
                if (value == null)
                {
                    if (obj[1] == null)
                    {
                        return true;
                    }
                }
                else if (value.equals(obj[1]))
                {
                    return true;
                }
            }
            else // entry expired - take this opportunity to remove it
            {
                it.remove();
            }
        }

        return false;
    }

    /* Map.entrySet() - not implemented */
    public Set entrySet()
    {
        throw new RuntimeException("This method is not yet implemented.");
    }

    /* implements Map.isEmpty() */
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    /* implements Map.keySet() */
    public Set keySet()
    {
        return map.keySet();
    }

    /**
     * Implements Map.putAll(Map)
     *
     * Put all Map entries into this ExpiryHashMap, with a current timestamp.
     */
    public void putAll(Map inMap)
    {
        Long timestamp = new Long(System.currentTimeMillis());
        Iterator it = inMap.keySet().iterator();
        while (it.hasNext())
        {
            Object key = it.next();

            Object[] obj = new Object[2];
            obj[0] = timestamp;
            obj[1] = inMap.get(key);

            this.map.put(key, obj);
        }
    }

    /* implements Map.remove(Object) */
    public Object remove(Object key)
    {
        Object[] obj = (Object[]) map.remove(key);

        if (obj != null && !isExpired(obj[0]))
            return obj[1];

        return null;
    }

    /* implements Map.size() */
    public int size()
    {
        return map.size();
    }

    /**
     * Implements Map.values()
     *
     * Returns a Collection of value objects that are still valid (not timed out).
     */
    public Collection values()
    {
        List vcoll = new ArrayList();
        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            Object key = it.next();
            Object[] obj = (Object[]) map.get(key);

            if (obj != null)
            {
                if (!isExpired(obj[0]))
                {
                    vcoll.add(obj[1]);
                }
                else // expired - take this opportunity to remove it
                {
                    map.remove(key);
                }
            }
        }

        return vcoll;
    }

    /**
     * Sets this instance to automatically (and periodically) timeout entries
     * in the cache, whenever a get() or put() operation is actioned.
     */
    public void setAutoTimeout(boolean autoTimeout)
    {
        this.autoTimeout = autoTimeout;
    }

    public boolean isAutoTimeout()
    {
        return this.autoTimeout;
    }

    /**
     * Check the timestamp object for expiry.
     */
    private boolean isExpired(Object timestamp)
    {
        return (((Long)timestamp).longValue() + timeout < System.currentTimeMillis());
    }
}
