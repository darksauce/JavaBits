package com.magi.io;

import java.io.File;
import java.io.FileFilter;

public class WildcardFileFilter implements FileFilter {

    private String[] filePatterns;
    private boolean  includeSubdirs;

    public WildcardFileFilter(String filePattern, boolean includeSubdirs) {
        this.filePatterns   = new String[] { filePattern };
        this.includeSubdirs = includeSubdirs;
    }

    public WildcardFileFilter(String[] filePatterns, boolean includeSubdirs) {
        this.filePatterns    = filePatterns;
        this.includeSubdirs = includeSubdirs;
    }
    
    public void setIncludeSubdirs(boolean includeSubdirs) {
        this.includeSubdirs = includeSubdirs;
    }

    public boolean getIncludeSubdirs() {
        return includeSubdirs;
    }

    public String[] getFilePatterns() {
        return filePatterns;
    }

    public void setFilePattern(String pattern) {
        this.filePatterns = new String[] { pattern };
    }

    public void setFilePatterns(String[] patterns) {
        this.filePatterns = patterns;
    }
    
    public boolean accept(File pathname) {

        if (pathname.isDirectory())
            return includeSubdirs;
        
        String name = pathname.getName();

        for (String filePattern: filePatterns) {
            if (isPatternMatch(name, filePattern)) {
            	return true;
            }
        }
        
        return false;
    }

    private boolean isPatternMatch(String name, String filePattern) {
    	boolean seek = false;
    	boolean wasseek = false;
    	int seek_i = 0;
        int n = 0;
        for (int i = 0; i < filePattern.length(); i++) {

            char p = filePattern.charAt(i);

            if (p != '*' && n == name.length())
                return false;

            switch (p) {
                case '*':
                    seek = true;
                    seek_i = i;
                    break;
                case '?':
                    n++;
                    seek = false;
                    break;
                default:
                    if (name.charAt(n) == p) {
                        n++;
                        wasseek = seek;
                        seek = false;
                    }
                    else if (seek) {
                        n++;
                        i--;
                    }
                    else {
                    	if (wasseek) {
                    		seek = wasseek;
                    		wasseek = false;
                    		i = seek_i;
                    	}
                    	else {
                            return false;
                    	}
                    }
                    break;
            }
        }

        if (!seek && n < name.length())
            return false;

        return true;
	}

	public static void main(String[] args) {
        WildcardFileFilter wff = new WildcardFileFilter("*.jpg", false);
        System.out.println("*.jpg - myimage.jpg = " + wff.accept(new File("myimage.jpg")));
        System.out.println("*.jpg - myimage.jpeg = " + wff.accept(new File("myimage.jpeg")));
        System.out.println("*.jpg - .jpg = " + wff.accept(new File(".jpg")));
        System.out.println("*.jpg - my.jpgx = " + wff.accept(new File("my.jpgx")));

        wff = new WildcardFileFilter("*name*", false);
        System.out.println("*name* - mynameis.txt = " + wff.accept(new File("mynameis.txt")));
        System.out.println("*name* - a.name = " + wff.accept(new File("a.name")));

        wff = new WildcardFileFilter("*.*", false);
        System.out.println("*.* - myimage.jpg = " + wff.accept(new File("myimage.jpg")));
        System.out.println("*.* - projectfile = " + wff.accept(new File("projectfile")));

        wff = new WildcardFileFilter("h?l?o.jpg", false);
        System.out.println("h?l?o.jpg - hello.jpg = " + wff.accept(new File("hello.jpg")));
        System.out.println("h?l?o.jpg - helllo.jpg = " + wff.accept(new File("helllo.jpg")));
        
        wff = new WildcardFileFilter("au.com.magi*.jar", false);
        System.out.println("au.com.magi*.jar - au.com.magi.wildcardapp.jar = " + wff.accept(new File("au.com.magi.wildcardapp.jar")));

        wff = new WildcardFileFilter("*.jar", false);
        System.out.println("*.jar - au.com.magi.wildcardapp.jar = " + wff.accept(new File("au.com.magi.wildcardapp.jar")));
    }
}
