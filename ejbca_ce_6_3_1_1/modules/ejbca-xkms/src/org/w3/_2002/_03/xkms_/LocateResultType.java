
package org.w3._2002._03.xkms_;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LocateResultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LocateResultType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2002/03/xkms#}ResultType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.w3.org/2002/03/xkms#}UnverifiedKeyBinding" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocateResultType", propOrder = {
    "unverifiedKeyBinding"
})
public class LocateResultType
    extends ResultType
{

    @XmlElement(name = "UnverifiedKeyBinding")
    protected List<UnverifiedKeyBindingType> unverifiedKeyBinding;

    /**
     * Gets the value of the unverifiedKeyBinding property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the unverifiedKeyBinding property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnverifiedKeyBinding().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UnverifiedKeyBindingType }
     * 
     * 
     */
    public List<UnverifiedKeyBindingType> getUnverifiedKeyBinding() {
        if (unverifiedKeyBinding == null) {
            unverifiedKeyBinding = new ArrayList<UnverifiedKeyBindingType>();
        }
        return this.unverifiedKeyBinding;
    }

}
