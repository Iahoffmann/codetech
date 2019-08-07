package codetech

class Supplier {
    String name

    static constraints = {
        name blank: false, size: 1..20
    }

    String toString() {
        name
    }
}
