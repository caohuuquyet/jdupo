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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ctLiteral complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ctLiteral">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="atom" type="{http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd}stAtom" minOccurs="0"/>
 *         &lt;element name="mode" type="{http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd}stMode" minOccurs="0"/>
 *         &lt;element name="not" maxOccurs="2" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element name="atom" type="{http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd}stAtom"/>
 *                   &lt;element name="mode" type="{http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd}stMode"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="interval" type="{http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd}ctInterval" minOccurs="0"/>
 *         &lt;element name="predicates" type="{http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd}ctPredicates" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ctLiteral", namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd", propOrder = {
    "atom",
    "mode",
    "not",
    "interval",
    "predicates"
})
public class CtLiteral {

    @XmlElement(namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd")
    protected String atom;
    @XmlElement(namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd")
    protected String mode;
    @XmlElement(namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd")
    protected List<CtLiteral.Not> not;
    @XmlElement(namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd")
    protected CtInterval interval;
    @XmlElement(namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd")
    protected CtPredicates predicates;

    /**
     * Gets the value of the atom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtom() {
        return atom;
    }

    /**
     * Sets the value of the atom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtom(String value) {
        this.atom = value;
    }

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMode(String value) {
        this.mode = value;
    }

    /**
     * Gets the value of the not property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the not property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNot().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CtLiteral.Not }
     * 
     * 
     */
    public List<CtLiteral.Not> getNot() {
        if (not == null) {
            not = new ArrayList<CtLiteral.Not>();
        }
        return this.not;
    }

    /**
     * Gets the value of the interval property.
     * 
     * @return
     *     possible object is
     *     {@link CtInterval }
     *     
     */
    public CtInterval getInterval() {
        return interval;
    }

    /**
     * Sets the value of the interval property.
     * 
     * @param value
     *     allowed object is
     *     {@link CtInterval }
     *     
     */
    public void setInterval(CtInterval value) {
        this.interval = value;
    }

    /**
     * Gets the value of the predicates property.
     * 
     * @return
     *     possible object is
     *     {@link CtPredicates }
     *     
     */
    public CtPredicates getPredicates() {
        return predicates;
    }

    /**
     * Sets the value of the predicates property.
     * 
     * @param value
     *     allowed object is
     *     {@link CtPredicates }
     *     
     */
    public void setPredicates(CtPredicates value) {
        this.predicates = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;choice>
     *         &lt;element name="atom" type="{http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd}stAtom"/>
     *         &lt;element name="mode" type="{http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd}stMode"/>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "atom",
        "mode"
    })
    public static class Not {

        @XmlElement(namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd")
        protected String atom;
        @XmlElement(namespace = "http://spin.nicta.org.au/spindle/spindleDefeasibleTheory2.xsd")
        protected String mode;

        /**
         * Gets the value of the atom property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAtom() {
            return atom;
        }

        /**
         * Sets the value of the atom property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAtom(String value) {
            this.atom = value;
        }

        /**
         * Gets the value of the mode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMode() {
            return mode;
        }

        /**
         * Sets the value of the mode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMode(String value) {
            this.mode = value;
        }

    }

}
