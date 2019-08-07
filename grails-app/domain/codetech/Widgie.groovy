package codetech

class Widgie {
    String name
    Supplier supplier

    static constraints = {
        name blank: false, size: 1..100
    }

    String toString() {
        name
    }
}
