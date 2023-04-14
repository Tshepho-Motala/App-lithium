
package com.id3global.id3gws._2013._04;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DownloadStoredImageResult" type="{http://schemas.microsoft.com/Message}StreamBody"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "downloadStoredImageResult"
})
@XmlRootElement(name = "DownloadStoredImageResponse")
public class DownloadStoredImageResponseElement {

    @XmlElement(name = "DownloadStoredImageResult", required = true)
    protected byte[] downloadStoredImageResult;

    /**
     * Gets the value of the downloadStoredImageResult property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getDownloadStoredImageResult() {
        return downloadStoredImageResult;
    }

    /**
     * Sets the value of the downloadStoredImageResult property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setDownloadStoredImageResult(byte[] value) {
        this.downloadStoredImageResult = value;
    }

}
