package codetech

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(WidgieService)
class WidgieServiceSpec extends Specification {
    static Color RED = new Color(name: 'Red').with { id = 1; delegate }
    static Color ORANGE = new Color(name: 'Orange').with { id = 2; delegate }

    static Supplier THREEM = new Supplier(name: '3M').with { id = 1; delegate }
    static Supplier BLUEBIRD = new Supplier(name: 'Bluebird').with { id = 2; delegate }

    static Widgie RED_LETTER_WIDGIE = new Widgie(
        name: 'Front assembly R/G/B 3M 40-12WN.6',
    )
    static Widgie RED_NUMBER_WIDGIE = new Widgie(
        name: 'Side booster unit 1/3/4/5/6/7 RO 432-11DWNE.17',
    )
    static Widgie ORANGE_WIDGIE = new Widgie(
        name: 'Front harness O/Y/G/B/I/V YA 490-6EWNE.20',
    )

    def setup() {
        Supplier.metaClass.static.findByName = { name ->
            switch (name) {
                case ~/(?i)3m/: return THREEM
                case ~/(?i)bluebird/: return BLUEBIRD
                default: return null
            }
        }
        Color.metaClass.static.findByName = { name ->
            switch (name) {
                case ~/(?i)red/: return RED
                case ~/(?i)orange/: return ORGANGE
                default: return null
            }
        }
    }

    def cleanup() {
    }

    // buildWhereClauseForWidgieNameQuery

    void "empty where clause for null widgie name query"() {
        expect: service.buildWhereClauseForWidgieNameQuery(null) == ['', [:]]
    }

    void "empty where clause for empty widgie name query"() {
        expect: service.buildWhereClauseForWidgieNameQuery('') == ['', [:]]
    }

    void "empty where clause for whitespace-only widgie name query"() {
        expect: service.buildWhereClauseForWidgieNameQuery('  ') == ['', [:]]
    }

    void "one-word where clause for one-word widgie name query"() {
        expect: service.buildWhereClauseForWidgieNameQuery('foo') == [
            '(LOWER(name) LIKE LOWER(:word0))',
            [word0: '%foo%']
        ]
    }

    void "one-word where clause for one-word plus whitespace widgie name query"() {
        expect: service.buildWhereClauseForWidgieNameQuery('  foo  ') == [
            '(LOWER(name) LIKE LOWER(:word0))',
            [word0: '%foo%']
        ]
    }

    void "three-word where clause for three-word widgie name query"() {
        expect: service.buildWhereClauseForWidgieNameQuery('foo bar baz') == [
            '(LOWER(name) LIKE LOWER(:word0)) AND (LOWER(name) LIKE LOWER(:word1)) AND (LOWER(name) LIKE LOWER(:word2))',
            [
                word0: '%foo%',
                word1: '%bar%',
                word2: '%baz%',
            ]
        ]
    }

    void "one-supplier where clause for one-supplier widgie name query"() {
        setup:
        expect: service.buildWhereClauseForWidgieNameQuery('bluebird') == [
            '(name LIKE :supplier0)',
            [supplier0: '%BL%']
        ]
    }

    void "one-color where clause for one-color widgie name query"() {
        setup:
        expect: service.buildWhereClauseForWidgieNameQuery('red') == [
            '(name LIKE :singleColor0 OR name LIKE :leadingColor0 OR name LIKE :middleColor0 OR name LIKE :trailingColor0)',
            [
                singleColor0: '% R %',
                leadingColor0: '% R/%',
                middleColor0: '%/R/%',
                trailingColor0: '%/R %',
            ]
        ]
    }

    void "supplier, color, and word where clause for supplier, color, and word widgie name query"() {
        expect: service.buildWhereClauseForWidgieNameQuery('red bluebird bell') == [
            '(name LIKE :singleColor0 OR name LIKE :leadingColor0 OR name LIKE :middleColor0 OR name LIKE :trailingColor0) AND (name LIKE :supplier1) AND (LOWER(name) LIKE LOWER(:word2))',
            [
                singleColor0: '% R %',
                leadingColor0: '% R/%',
                middleColor0: '%/R/%',
                trailingColor0: '%/R %',
                supplier1: '%BL%',
                word2: '%bell%',
            ]
        ]
    }

    // listColorIdsFromWidgieName

    void "empty color ids in null widgie name"() {
        expect: service.listColorIdsFromWidgieName(null) == []
    }

    void "empty color ids in empty widgie name"() {
        expect: service.listColorIdsFromWidgieName('') == []
    }

    void "empty color ids in invalid widgie name"() {
        expect: service.listColorIdsFromWidgieName('foo') == []
    }

    void "letter color ids in widgie name with single letter color"() {
        expect: service.listColorIdsFromWidgieName(
            'Primary wing nut I WO 728-14OT.13') == ['I']
    }

    void "number color ids in widgie name with single number color"() {
        expect: service.listColorIdsFromWidgieName(
            'Primary wing nut 6 RO 504-14OT.11') == ['6']
    }

    void "letter color ids in widgie name with multiple letter colors"() {
        expect: service.listColorIdsFromWidgieName(
            'Front assembly Y/G/B 3M 40-12WN.6') == ['Y', 'G', 'B']
    }

    void "number color ids in widgie name with multiple number colors"() {
        expect: service.listColorIdsFromWidgieName(
            'Front assembly 6/7 GO 240-12OT.9') == ['6', '7']
    }

    // colorInWidgie

    void "null color not in null widgie"() {
        expect: !service.colorInWidgie(null, null)
    }

    void "red color not in null widgie"() {
        expect: !service.colorInWidgie(RED, null)
    }

    void "null color not in red letter widgie"() {
        expect: !service.colorInWidgie(null, RED_LETTER_WIDGIE)
    }

    void "null color not in red number widgie"() {
        expect: !service.colorInWidgie(null, RED_NUMBER_WIDGIE)
    }

    void "red color not in orange widgie"() {
        expect: !service.colorInWidgie(RED, ORANGE_WIDGIE)
    }

    void "red color in red letter widgie"() {
        expect: service.colorInWidgie(RED, RED_LETTER_WIDGIE)
    }

    void "red color in red number widgie"() {
        expect: service.colorInWidgie(RED, RED_NUMBER_WIDGIE)
    }
}
