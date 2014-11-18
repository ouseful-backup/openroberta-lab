package de.fhg.iais.roberta.hardwarecomponents;

import de.fhg.iais.roberta.dbc.Assert;

/**
 * This is a top class of all hardware components that can be connected to any kind of brick. All more specific hardware components must extend
 * {@link HardwareComponentType}.
 */
public abstract class HardwareComponentType {
    private final String name;
    private final Category category;

    /**
     * This constructor sets the name of the blockly block that maps to this component and {@link Category} of the hardware component.
     *
     * @param name
     * @param category
     */
    public HardwareComponentType(String name, Category category) {
        Assert.isTrue(!name.equals("") && category != null);
        this.category = category;
        this.name = name;
    }

    /**
     * Get the category in which belongs. See enum {@link Category} for all categories.
     *
     * @return the category
     */
    public Category getCategory() {
        return this.category;
    }

    /**
     * The name of the blockly block mapped to this hardware component.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * The name of the component.
     *
     * @return the name
     */
    public abstract String getTypeName();

    /**
     * @return valid Java code name of the enumeration
     */
    public abstract String getJavaCode();

    @Override
    public String toString() {
        return "HardwareComponentType [" + this.name + ", " + this.category + "]";
    }
}