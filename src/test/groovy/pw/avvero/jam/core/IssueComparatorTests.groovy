package pw.avvero.jam.core

import pw.avvero.jam.core.IssueComparator
import pw.avvero.jam.core.IssueComparisonException
import pw.avvero.jam.schema.Issue
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static pw.avvero.jam.core.Difference.NEW_ISSUE

class IssueComparatorTests extends Specification {

    @Shared
    def comparator = new IssueComparator()

    @Unroll
    def "Comparison between #from and #to ends with error"() {
        when:
        comparator.compare(from, to)
        then:
        def e = thrown(IssueComparisonException)
        e.message == message
        where:
        from                      | to                        || message
        null                      | null                      || "Can't compare empty issues"
        null                      | new Issue()               || "Can't compare empty issues"
        new Issue()               | null                      || "Can't compare empty issues"
        new Issue()               | new Issue()               || "Can't compare keyless issues"
        new Issue()               | new Issue(key: "WATCH-1") || "Can't compare keyless issues"
        new Issue(key: "WATCH-1") | new Issue()               || "Can't compare keyless issues"
        new Issue(key: "WATCH-1") | new Issue(key: "WATCH-2") || "Issues has different keys"
    }

    def "New issue is expected if diff"() {
        when:
        def diff = comparator.compare(from, to)
        then:
        diff.size() == 1
        diff[0].type == NEW_ISSUE
        diff[0].newValue == new Issue(project: "A", type: "Story", summary: "Do 2")
        where:
        from = new Issue(project: "A", type: "Story", key: "A-1", summary: "Do 1")
        to = new Issue(project: "A", type: "Story", key: "A-1", summary: "Do 1",
                children: [new Issue(project: "A", type: "Task", summary: "Do 2")])
    }

}