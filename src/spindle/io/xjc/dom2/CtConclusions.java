/**
 * SPINdle (version 2.2.4)
 * Copyright (C) 2009-2014 NICTA Ltd.
 *
 * This file is part of SPINdle project.
 * 
 * SPINdle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SPINdle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SPINdle.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author H.-P. Lam (oleklam@gmail.com), National ICT Australia - Queensland Research Laboratory 
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.07 at 09:53:03 AM EST 
//


package spindle.io.xjc.dom2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ctConclusions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ctConclusions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;sequence>
 *           &lt;element name="conclusion" type="{http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd}ctConclusion"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ctConclusions", namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd", propOrder = {
    "description",
    "conclusion"
})
public class CtConclusions {

    @XmlElement(namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd", required = true)
    protected String description;
    @XmlElement(namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd", required = true)
    protected CtConclusion conclusion;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the conclusion property.
     * 
     * @return
     *     possible object is
     *     {@link CtConclusion }
     *     
     */
    public CtConclusion getConclusion() {
        return conclusion;
    }

    /**
     * Sets the value of the conclusion property.
     * 
     * @param value
     *     allowed object is
     *     {@link CtConclusion }
     *     
     */
    public void setConclusion(CtConclusion value) {
        this.conclusion = value;
    }

}
