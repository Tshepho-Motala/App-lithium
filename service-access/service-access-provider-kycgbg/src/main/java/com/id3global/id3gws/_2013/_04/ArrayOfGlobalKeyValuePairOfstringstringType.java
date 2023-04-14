
package com.id3global.id3gws._2013._04;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfGlobalKeyValuePairOfstringstring complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGlobalKeyValuePairOfstringstring"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GlobalKeyValuePairOfstringstring" type="{http://www.id3global.com/ID3gWS/2013/04}GlobalKeyValuePairOfstringstring" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGlobalKeyValuePairOfstringstring", propOrder = {
    "globalKeyValuePairOfstringstring"
})
public class ArrayOfGlobalKeyValuePairOfstringstringType {

    @XmlElement(name = "GlobalKeyValuePairOfstringstring")
    protected List<GlobalKeyValuePairOfstringstringType> globalKeyValuePairOfstringstring;

    /**
     * Gets the value of the globalKeyValuePairOfstringstring property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the globalKeyValuePairOfstringstring property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGlobalKeyValuePairOfstringstring().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GlobalKeyValuePairOfstringstringType }
     * 
     * 
     */
    public List<GlobalKeyValuePairOfstringstringType> getGlobalKeyValuePairOfstringstring() {
        if (globalKeyValuePairOfstringstring == null) {
            globalKeyValuePairOfstringstring = new ArrayList<GlobalKeyValuePairOfstringstringType>();
        }
        return this.globalKeyValuePairOfstringstring;
    }

}
