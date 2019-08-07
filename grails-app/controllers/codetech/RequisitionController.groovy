package codetech

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class RequisitionController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Requisition.list(params), model:[requisitionCount: Requisition.count()]
    }

    def show(Requisition requisition) {
        respond requisition
    }

    def create() {
        [
            command: request.command ?: new CreateRequisitionCommand(),
            colors: Color.list(),
            suppliers: Supplier.list(),
        ]
    }

    @Transactional
    def save(CreateRequisitionCommand command) {
        if (command.hasErrors()) {
            transactionStatus.setRollbackOnly()
            request.command = command
            return forward(action: 'create')
        }

        def requisition = new Requisition(
            units: command.units,
            color: command.color,
            widgie: command.widgie,
        )
        requisition.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(
                    code: 'default.created.message',
                    args: [
                        message(code: 'requisition.label', default: 'Requisition'),
                        requisition,
                    ],
                )
                redirect requisition
            }
            '*' { respond requisition, [status: CREATED] }
        }
    }

    def edit(Requisition requisition) {
        respond requisition
    }

    @Transactional
    def update(Requisition requisition) {
        if (requisition == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (requisition.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond requisition.errors, view:'edit'
            return
        }

        requisition.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'requisition.label', default: 'Requisition'), requisition.id])
                redirect requisition
            }
            '*'{ respond requisition, [status: OK] }
        }
    }

    @Transactional
    def delete(Requisition requisition) {

        if (requisition == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        requisition.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'requisition.label', default: 'Requisition'), requisition.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'requisition.label', default: 'Requisition'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
