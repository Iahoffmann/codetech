package codetech

class Requisition {
    int units = 1
    Color color
    Widgie widgie

    static constraints = {
        units range: 1..999
    }

    String toString() {
        "$units $color ${widgie?.supplier} $widgie"
    }
}
