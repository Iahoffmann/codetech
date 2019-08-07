package codetech

import grails.transaction.Transactional
import groovy.sql.Sql

@Transactional
class WidgieService {
    def dataSource

    /**
     * Returns two-element list with where clause
     * @param query Free-form query string (ie 'red rockwell relay').
     * @return Two-element list:
     * first element is WHERE clause (ie
     * "(name LIKE :color0) AND (name LIKE :supplier1) AND (name LIKE :word2)");
     * second element is SQL parameter map (ie
     * [color0:'%/R/%', supplier1:'%RO%', word2:'%relay%']).
     */
    List buildWhereClauseForWidgieNameQuery(String query) {
        // split query into individual words
        def words = query?.split(/ +/).findAll()
        // return empty result if no words
        if (!words) return ['', [:]]

        // initialize empty map of SQL query params
        def params = [:]
        // initialize empty list of WHERE clauses, to be joined by ANDs
        def where = []

        // for each word build up WHERE clauses
        for (int i = 0; i < words.size(); i++) {
            def word = words[i]

            // if word is a supplier
            def supplier = Supplier.findByName(word.toLowerCase().capitalize())
            if (supplier) {
                // add first two letters of supplier as a named parameter
                params."supplier${i}" = "%${supplier.name[0..1].toUpperCase()}%" as String
                // append supplier check to list of WHERE clauses
                where << "name LIKE :supplier${i}"
                continue
            }

            // if word is a color
            def color = Color.findByName(word.toLowerCase().capitalize())
            if (color) {
                // initialize empty list of subclauses, to be joined by ORs
                def orColor = []

                // single color ('R' in 'part R supplier etc')
                params."singleColor${i}" = "% ${color.name[0]} %" as String
                orColor << "name LIKE :singleColor${i}"

                // leading color ('R' in 'part R/O/Y supplier etc')
                params."leadingColor${i}" = "% ${color.name[0]}/%" as String
                orColor << "name LIKE :leadingColor${i}"

                // middle color ('O' in 'part R/O/Y supplier etc')
                params."middleColor${i}" = "%/${color.name[0]}/%" as String
                orColor << "name LIKE :middleColor${i}"

                // trailing color ('Y' in 'part R/O/Y supplier etc')
                params."trailingColor${i}" = "%/${color.name[0]} %" as String
                orColor << "name LIKE :trailingColor${i}"

                // append color check to list of WHERE clauses
                where << orColor.join(' OR ')
                continue
            }

            // otherwise word is any part of widgie name
            // add full word as a named parameter
            params."word${i}" = "%${word}%" as String
            // append general name check to list of WHERE clauses
            where << "LOWER(name) LIKE LOWER(:word${i})"
        }

        // join list of WHERE clauses into full string
        def whereClause = where.collect { "($it)" }.join(' AND ')
        // return two-element list: where clause and SQL params
        return [whereClause, params]
    }

    /**
     * Returns list of widgies which match the specified query string.
     * @param query Free-form query string (ie 'red rockwell relay').
     * @return List of widgies, or empty list.
     */
    List<Widgie> searchForWidgies(String query, int max = 10, int offset = 0) {
        def (whereClause, params) = buildWhereClauseForWidgieNameQuery(query)
        // return empty list if nothing to search for
        if (!whereClause) return []

        // initialize groovy SQL API with app's SQL datasource config settings
        def sql = new Sql(dataSource)
        // execute SQL query, return result set as list of rows
        def rows = sql.rows("""
            SELECT id FROM widgie
            WHERE ${whereClause}
            ORDER BY id
            LIMIT ${offset},${max}
        """.toString(), params)

        // initialize empty list of Widgie results
        def result = []
        // for each result set row lookup Widgie object
        for (int i = 0; i < rows.size(); i++) {
            // lookup each Widgie by its id
            result << Widgie.get(rows[i].id)
        }
        return result
    }

    /**
     * Returns count of widgies which match the specified query string.
     * @param query Free-form query string (ie 'red rockwell relay').
     * @return Count of widgies, or 0.
     */
    int countWidgies(String query) {
        def (whereClause, params) = buildWhereClauseForWidgieNameQuery(query)
        // return 0 if nothing to search for
        if (!whereClause) return 0

        // initialize groovy SQL API with app's SQL datasource config settings
        def sql = new Sql(dataSource)
        // execute SQL query, return result set as list of rows
        def row = sql.firstRow("""
            SELECT COUNT(*) FROM widgie
            WHERE ${whereClause}
        """.toString(), params)

        // return first column (the count) of the SQL query
        return row[0]
    }

    /**
     * Parses color ids or first letter of color names from a widgie name.
     * @param widgieName Widgie name (eg 'Front assembly Y/G/B 3M 40-12WN.6').
     * @return List of ids (as strings) or first letters (eg ['Y','G','B']).
     */
    List<String> listColorIdsFromWidgieName(String widgieName) {
        (widgieName =~ / ([A-Z0-9](?:\/[A-Z0-9])*) [A-Z0-9]{2} /).collect {
            it[1]
        }.find()?.split('/') as List ?: []
    }

    /**
     * True if the specified color is valid for the specified widgie.
     * @param color Color to check.
     * @param widgie Widgie to check.
     * @return True if color in widgie.
     */
    boolean colorInWidgie(Color color, Widgie widgie) {
        def ids = listColorIdsFromWidgieName(widgie?.name)
        (color?.id as String) in ids || color?.name.find() in ids
    }
}
