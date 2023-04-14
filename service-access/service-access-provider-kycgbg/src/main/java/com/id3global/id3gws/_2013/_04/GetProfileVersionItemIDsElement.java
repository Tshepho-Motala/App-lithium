
package com.id3global.id3gws._2013._04;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
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
 *         &lt;element name="OrgID" type="{http://schemas.microsoft.com/2003/10/Serialization/}guid" minOccurs="0"/&gt;
 *         &lt;element name="ProfileIdVersion" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalProfileIDVersion" minOccurs="0"/&gt;
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
    "orgID",
    "profileIdVersion"
})
@XmlRootElement(name = "GetProfileVersionItemIDs")
public class GetProfileVersionItemIDsElement {

    @XmlElement(name = "OrgID")
    protected String orgID;
    @XmlElementRef(name = "ProfileIdVersion", namespace = "http://www.id3global.com/ID3gWS/2013/04", type = JAXBElement.class, required = false)
    protected JAXBElement<GlobalProfileIDVersionType> profileIdVersion;

    /**
     * Gets the value of the orgID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgID() {
        return orgID;
    }

    /**
     * Sets the value of the orgID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgID(String value) {
        this.orgID = value;
    }

    /**
     * Gets the value of the profileIdVersion property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link GlobalProfileIDVersionType }{@code >}
     *     
     */
    public JAXBElement<GlobalProfileIDVersionType> getProfileIdVersion() {
        return profileIdVersion;
    }

    /**
     * Sets the value of the profileIdVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link GlobalProfileIDVersionType }{@code >}
     *     
     */
    public void setProfileIdVersion(JAXBElement<GlobalProfileIDVersionType> value) {
        this.profileIdVersion = value;
    }

}
