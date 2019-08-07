package codetech

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class SupplierController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Supplier.list(params), model:[supplierCount: Supplier.count()]
    }

    def show(Supplier supplier) {
        respond supplier
    }

    def create() {
        respond new Supplier(params)
    }

    @Transactional
    def save(Supplier supplier) {
        if (supplier == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (supplier.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond supplier.errors, view:'create'
            return
        }

        supplier.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'supplier.label', default: 'Supplier'), supplier.id])
                redirect supplier
            }
            '*' { respond supplier, [status: CREATED] }
        }
    }

    def edit(Supplier supplier) {
        respond supplier
    }

    @Transactional
    def update(Supplier supplier) {
        if (supplier == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (supplier.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond supplier.errors, view:'edit'
            return
        }

        supplier.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'supplier.label', default: 'Supplier'), supplier.id])
                redirect supplier
            }
            '*'{ respond supplier, [status: OK] }
        }
    }

    @Transactional
    def delete(Supplier supplier) {

        if (supplier == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        supplier.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'supplier.label', default: 'Supplier'), supplier.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'supplier.label', default: 'Supplier'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
