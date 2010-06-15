package geoscript.style

import org.geotools.styling.FeatureTypeStyle

/**
 * A SubStyle wraps a GeoTools SLD FeatureTypeStyle
 * @author Jared Erickson
 */
class SubStyle {

    /**
     * The GeoTools FeatureTypeStyle
     */
    FeatureTypeStyle featureTypeStyle

    /**
     * Create a SubStyle with a GeoTools FeatureTypeStyle
     * @param featureTypeStyle The GeoTools FeatureTypeStyle
     */
    SubStyle(FeatureTypeStyle featureTypeStyle) {
        this.featureTypeStyle = featureTypeStyle
        setName("simple")
    }

    /**
     * Create a SubStyle with a Rule
     * @param rule The Rule
     */
    SubStyle(Rule rule) {
        this(Style.builder.createFeatureTypeStyle("Feature", rule.rule))
    }

    /**
     * Create a SubStyle with a Symbolizer
     * @param symbolizer The Symbolizer
     */
    SubStyle(Symbolizer symbolizer) {
        this(new Rule(symbolizer))
    }

    /**
     * Create a SubStyle from a List of Rules or Symbolizers
     * @param rules A List of Rules or Symbolizers
     */
    SubStyle(List list) {
        this(createFeatureTypeStyle(list))
    }

    /**
     * Create a GeoTools FeatureTypeStyle from a List of Rules or Symbolizers
     * @param list A List of Rules of Symbolizers
     * @return A GeoTools FeatureTypeStyle
     */
    private static FeatureTypeStyle createFeatureTypeStyle(List list) {
        def firstItem = list[0]
        if (firstItem instanceof Rule) {
            return createFeatureTypeStyleFromRules(list)
        }
        else {
            return createFeatureTypeStyleFromRules(list.collect{sym->new Rule(sym)})
        }
    }

        /**
     * Create a GeoTools FeatureTypeStyle from a List of Rules
     * @param rules A List of Rules
     * @return A GeoTools FeatureTypeStyle
     */
    private static FeatureTypeStyle createFeatureTypeStyleFromRules(List<Rule> rules) {
        Style.builder.createFeatureTypeStyle("Feature", rules.collect{rule->
            rule.rule
        }.toArray() as org.geotools.styling.Rule[])
    }


    /**
     * Add a List of Rules
     * @param A List of Rules
     */
    void addRules(List<Rule> rules) {
        featureTypeStyle.rules().addAll(rules.collect{r->r.rule})
    }

    /**
     * Get the List of Rules
     * @return The List of Rules
     */
    List<Rule> getRules() {
       featureTypeStyle.rules().collect{r->new Rule(r)}
    }

    /**
     * Get the name
     * @return The name
     */
    String getName() {
        featureTypeStyle.name
    }

    /**
     * Set the name
     * @param name The new name
     */
    void setName(String name) {
        featureTypeStyle.name = name
    }

    /**
     * Get the title
     * @return The title
     */
    String getTitle() {
        featureTypeStyle.description.title
    }

    /**
     * Set the title
     * @param title The new title
     */
    void setTitle(String title) {
        featureTypeStyle.description.title = title
    }

    /**
     * Get the abstract
     * @return The abstract
     */
    String getAbstract() {
        featureTypeStyle.description.getAbstract()
    }

    /**
     * Set the abstract
     * @return abstractStr The new abstract
     */
    void setAbstract(String abstractStr) {
        featureTypeStyle.description.setAbstract(abstractStr)
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        //getName() + " (" + getTitle() + ") " + getAbstract()
        featureTypeStyle.toString()
    }

}

