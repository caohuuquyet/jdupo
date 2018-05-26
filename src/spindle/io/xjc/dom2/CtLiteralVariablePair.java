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
 * <p>Java class for ctLiteralVariablePair complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ctLiteralVariablePair">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd}ctLiteralVariable"/>
 *         &lt;element name="value" type="{http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd}ctLiteralVariable"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ctLiteralVariablePair", namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd", propOrder = {
    "name",
    "value"
})
public class CtLiteralVariablePair {

    @XmlElement(namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd", required = true)
    protected CtLiteralVariable name;
    @XmlElement(namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd", required = true)
    protected CtLiteralVariable value;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link CtLiteralVariable }
     *     
     */
    public CtLiteralVariable getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link CtLiteralVariable }
     *     
     */
    public void setName(CtLiteralVariable value) {
        this.name = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link CtLiteralVariable }
     *     
     */
    public CtLiteralVariable getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link CtLiteralVariable }
     *     
     */
    public void setValue(CtLiteralVariable value) {
        this.value = value;
    }

}