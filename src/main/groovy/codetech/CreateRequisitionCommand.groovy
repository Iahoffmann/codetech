package codetech

import grails.validation.Validateable
import grails.databinding.BindUsing

class CreateRequisitionCommand implements Validateable {
    def widgieService

    Integer units
    Color color
    Supplier supplier
    @BindUsing({ dst, src -> Widgie.findByName(src['widgie']) })
    Widgie widgie

    static constraints = {
        importFrom Requisition
        widgie validator: { value, command ->
            if (value?.supplier?.id != command.supplier?.id)
                return ['mismatch.supplier', command.supplier]
            if (!command.widgieService.colorInWidgie(command.color, value))
                return ['mismatch.color', command.color]
            return true
        }
    }
}
