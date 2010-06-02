package geoscript.style

import org.geotools.styling.FeatureTypeStyle

/**
 * A SubStyle wraps a GeoTools SLD FeatureTypeStyle
 * @author Jared Erickson
 */
class SubStyle {

    FeatureTypeStyle featureTypeStyle

    SubStyle(FeatureTypeStyle featureTypeStyle) {
        this.featureTypeStyle = featureTypeStyle
        setName("simple")
    }
    
    SubStyle(Rule rule) {
        this(Style.builder.createFeatureTypeStyle("Feature", rule.rule))
    }

    SubStyle(List<Rule> rules) {
        this(createFeatureTypeStyle(rules))
    }

    private static FeatureTypeStyle createFeatureTypeStyle(List<Rule> rules) {
        Style.builder.createFeatureTypeStyle(rules.collect{rule->
            rule.rule
        }.toArray() as org.geotools.styling.Rule[])
    }

    void addRules(List<Rule> rules) {
        featureTypeStyle.rules().addAll(rules.collect{r->r.rule})
    }

    List<Rule> getRules() {
       featureTypeStyle.rules().collect{r->new Rule(r)}
    }

    String getName() {
        featureTypeStyle.name
    }

    void setName(String name) {
        featureTypeStyle.name = name
    }

    String getTitle() {
        featureTypeStyle.description.title
    }

    void setTitle(String title) {
        featureTypeStyle.description.title = title
    }

    String getAbstract() {
        featureTypeStyle.description.getAbstract()
    }

    void setAbstract(String abstractStr) {
        featureTypeStyle.description.setAbstract(abstractStr)
    }

    String toString() {
        //getName() + " (" + getTitle() + ") " + getAbstract()
        featureTypeStyle.toString()
    }

}

