package codetech

class Color {
    String name

    static constraints = {
        name blank: false, size: 1..20
    }

    String toString() {
        name
    }
}
