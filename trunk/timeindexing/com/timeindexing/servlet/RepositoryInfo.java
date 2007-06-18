// RepositoryInfo.java

package com.timeindexing.servlet;

/**
 * A class that stores information about a repository.
 * <p>
 * It keeps:
 * <ul>
 * <li> the repository name </li>
 * <li> the path to the indexes for this repository </li>
 * <li> the path to the XML descriptors for this repository </li>
 * </ul>
 */
public class RepositoryInfo {
    /*
     * The name of the repository.
     */
    String name = null;

    /*
     * The index path of the repository.
     */
    String indexPath = null;

    /*
     * The descriptor path of the repository.
     */
    String descriptorPath = null;


    /**
     * Constructor for  a RepositoryInfo.
     */
    public RepositoryInfo(String name, String indexPath, String descriptorPath) {
	this.name = name;
	this.indexPath = indexPath;
	this.descriptorPath = descriptorPath;
    }

    /**
     * Get the  name of the repository.
     * @return the name
     */
    public String getName()  {
	return name;
    }

    /**
     * Sets the name of the repository.
     * @param argName Value to assign to name
     */
    public void setName(String argName) {
	name = argName;
    }

    /**
     * Gets the  index path of the repository.
     * @return the value of indexPath
     */
    public String getIndexPath()  {
	return indexPath;
    }

    /**
     * Sets the indexPath of the repository.
     * @param argIndexPath Value to assign to this.indexPath
     */
    public void setIndexPath(String argIndexPath) {
	indexPath = argIndexPath;
    }

    /**
     * Gets the descriptorPath of the repository.
     * @return the value of descriptorPath
     */
    public String getDescriptorPath()  {
	return descriptorPath;
    }

    /**
     * Sets the value of descriptorPath.
     * @param argDescriptorPath Value to assign to this.descriptorPath
     */
    public void setDescriptorPath(String argDescriptorPath) {
	this.descriptorPath = argDescriptorPath;
    }

}
