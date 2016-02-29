package dslContext

import groovy.util.logging.Slf4j
import migration.filter.criterias.Criteria
import net.praqma.clearcase.ucm.entities.Baseline as CoolBaseline

@Slf4j
class CriteriaContext implements Context {
    List<Criteria> criteria = []

    /**
     * Filters out baselines that were created before a baseline
     * @param name the name of the baseline
     */
    def void afterBaseline(String name) {
        afterDate(CoolBaseline.get(name).date)
    }

    /**
     * Filters out baselines that were created before a date
     * @param format the date format
     * @param date the date
     */
    def void afterDate(String format, String date) {
        afterDate(Date.parse(format, date))
    }

    /**
     * Filters out baselines that were created before a date
     * @param date the date
     */
    def void afterDate(Date date) {
        criteria.add(new Criteria() {
            @Override
            boolean appliesTo(CoolBaseline baseline) {
                println "Testing '" + baseline.shortname + " (" + baseline.date + ")' against date '" + date + "'."
                def result = baseline.date > date
                println "Result: " + (result ? "SUCCESS" : "FAILURE")
                return result
            }
        })
    }

    /**
     * Filters out baselines whose name don't match the regex
     * @param regex the regex to test baseline names against
     */
    def void baselineName(String regex) {
        criteria.add(new Criteria() {
            @Override
            boolean appliesTo(CoolBaseline baseline) {
                println "Testing '" + baseline.shortname + "' against regex '" + regex + "'."
                def matcher = baseline.shortname =~ regex
                def result = matcher.matches()
                println "Result: " + (result ? "SUCCESS" : "FAILURE")
                return result
            }
        })
    }

    /**
     * Filters out baselines using a custom Groovy closure
     * @param closure Closure to run, returns boolean, passes in the Baseline
     */
    def void custom(Closure<Boolean> closure){
        criteria.add(new Criteria() {
            @Override
            boolean appliesTo(CoolBaseline baseline) {
                println "Testing " + baseline.shortname + " using custom criteria."
                closure.delegate = this
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                def result = closure.call(baseline)
                println "Result: " + (result ? "SUCCESS" : "FAILURE")
                return result
            }
        })
    }

    /**
     * Filters out baselines that don't have given promotion levels
     * @param promotionLevels the promotion levels to test baselines against
     */
    def void promotionLevels(String... promotionLevels) {
        criteria.add(new Criteria() {
            @Override
            boolean appliesTo(CoolBaseline baseline) {
                println "Testing '" + baseline.shortname + " (" + baseline.promotionLevel + ")' against promotionLevels '" + promotionLevels + "'."
                def result = promotionLevels.contains(baseline.promotionLevel.toString())
                println "Result: " + (result ? "SUCCESS" : "FAILURE")
                return result
            }
        })
    }
}