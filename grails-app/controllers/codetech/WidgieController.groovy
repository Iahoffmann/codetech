package codetech

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class WidgieController {
    def widgieService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max, Integer offset, String query) {
        if (query) {
            def list = widgieService.searchForWidgies(
                query, Math.min(max ?: 10, 100), offset ?: 0)
            def count = widgieService.countWidgies(query)
            return respond(list, model: [widgieCount:count])
        }

        // no query: default list
        params.max = Math.min(max ?: 10, 100)
        respond Widgie.list(params), model:[widgieCount: Widgie.count()]
    }

    def show(Widgie widgie) {
        respond widgie
    }

    def create() {
        respond new Widgie(params)
    }

    @Transactional
    def save(Widgie widgie) {
        if (widgie == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (widgie.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond widgie.errors, view:'create'
            return
        }

        widgie.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'widgie.label', default: 'Widgie'), widgie.id])
                redirect widgie
            }
            '*' { respond widgie, [status: CREATED] }
        }
    }

    def edit(Widgie widgie) {
        respond widgie
    }

    @Transactional
    def update(Widgie widgie) {
        if (widgie == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (widgie.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond widgie.errors, view:'edit'
            return
        }

        widgie.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'widgie.label', default: 'Widgie'), widgie.id])
                redirect widgie
            }
            '*'{ respond widgie, [status: OK] }
        }
    }

    @Transactional
    def delete(Widgie widgie) {

        if (widgie == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        widgie.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'widgie.label', default: 'Widgie'), widgie.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'widgie.label', default: 'Widgie'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
