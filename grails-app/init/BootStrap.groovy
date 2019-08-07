import codetech.Color
import codetech.Requisition
import codetech.Supplier
import codetech.Widgie

class BootStrap {

    def init = { servletContext ->
        bootstrapColors()
        bootstrapSuppliers()
        bootstrapWidgies()
        bootstrapRequisitions()
    }
    def destroy = {
    }

    void bootstrapColors() {
        if (Color.count()) return
        log.info "bootstrap colors"

        'Red Orange Yellow Green Blue Indigo Violet'.split(/ /).each {
            new Color(name: it).save()
        }
    }

    void bootstrapSuppliers() {
        if (Supplier.count()) return
        log.info "bootstrap suppliers"

        '''
            3M Bluebird Cummins Diehl Foxconn Goepel Honda
            Pirelli Rockwell Suzuki Toro Unilever Wolfcraft Yamaha
        '''.trim().split(/\s+/).each {
            new Supplier(name: it).save()
        }
    }

    void bootstrapWidgies() {
        if (Widgie.count()) return

        parts.eachWithIndex { part, partIndex ->
            log.info "bootstrap $part widgie"
            def typeIndex = 0
            types.findAll { type ->
                typeIndex++
                (partIndex + 1) % (typeIndex + 1) &&
                (partIndex + 1) % (typeIndex + 3) &&
                (partIndex + 1) % (typeIndex + 5)
            }.each { type ->
                Supplier.list().findAll {
                    (part.charAt(0) as Integer) % Supplier.count() != it.id &&
                    (part.charAt(1) as Integer) % Supplier.count() != it.id
                }.each { supplier ->
                    def colors = (
                        Color.list().findAll {
                            (partIndex + supplier.id * 3) % (it.id / 3 + 1 as Integer)
                        } ?: Color.list().findAll {
                            (partIndex + supplier.id * 3) % (it.id / 3 + 2 as Integer)
                        } ?: Color.list().findAll {
                            (partIndex + supplier.id * 3) % Color.count() != it.id - 1
                        }
                    )
                    def colorNames = colors.collect {
                        supplier.id % 3 ? it.name[0] : it.id
                    }.join('/')

                    def supplierAbbr = supplier.name[0..1].toUpperCase()
                    def partNumber = [
                        part.length() * type.length() * supplier.id,
                        '-',
                        127 % part.length() + type.length(),
                        colors[0..(colors.size()/2)].collect {
                            it.name[-1].toUpperCase()
                        }.join(''),
                        '.',
                        colors.sum(supplier.id) { it.id } / 2 as Integer,
                    ].join('')

                    def name = [
                        type, part, colorNames, supplierAbbr, partNumber
                    ].join(' ')
                    new Widgie(name: name, supplier: supplier).save()
                }
            }
        }
    }

    void bootstrapRequisitions() {
        if (Requisition.count()) return
        log.info "bootstrap requisitions"

        new Requisition(
            units: 499,
            color: Color.findByName('Yellow'),
            widgie: Widgie.get(1),
        ).save()
        new Requisition(
            units: 1,
            color: Color.findByName('Indigo'),
            widgie: Widgie.get(2),
        ).save()
    }

    List<String> types = '''
        Front Back Side Top Bottom Small Oversized Rear
        Main Floor Interior Primary Secondary
    '''.trim().split(/\s+/) as List

    List<String> parts = '''
        assembly
        axle
        bag
        bar
        battery
        bearing
        bell
        belt
        bolt
        booster unit
        box
        bracket
        cable
        cage
        cap
        carpet
        casing
        cell
        chain
        connector
        console
        control system
        controller
        coupler
        cover
        cylinder
        damper
        disc
        door
        drawer
        drive
        duct
        fan
        fastener
        filter
        fuse
        gasket
        gate
        gauge
        grill
        guide
        handle
        harness
        hinge
        hose
        housing
        indicator
        latch
        lever
        lid
        light
        lining
        lock
        meter
        mounting
        name plate
        nozzle
        o-ring
        pad
        pan
        panel
        peg
        pin
        pipe
        piston
        plate
        plate
        plug
        pump
        rack
        rail
        regulator
        relay
        reservoir
        rim
        ring
        rod
        rotor
        screen
        seal
        sensor
        shim
        sight
        speaker
        spring
        strap
        switch
        tank
        terminal
        tray
        tube
        valve
        washer
        wheel
        window
        wing nut
    '''.trim().split(/\n+/).collect { it.trim() }
}
