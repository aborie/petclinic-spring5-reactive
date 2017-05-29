package com.yetanotherdevblog.petclinic.handlers

import com.yetanotherdevblog.html
import com.yetanotherdevblog.petclinic.model.Vet
import com.yetanotherdevblog.petclinic.repositories.SpecialityRepository
import com.yetanotherdevblog.petclinic.repositories.VetRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class VetsHandler(val vetRepository: VetRepository, val specialityRepository: SpecialityRepository) {

    fun goToVetsIndex(serverRequest: ServerRequest) = goToIndex()

    fun goToAdd(serverRequest: ServerRequest): Mono<ServerResponse> {
        return ok().html()
                .render("vets/add", mapOf("specialities" to specialityRepository.findAll()))
    }

    fun add(serverRequest: ServerRequest): Mono<ServerResponse> {
        return serverRequest.body(BodyExtractors.toFormData()).flatMap {
            formData ->
            vetRepository.save(Vet(
                    id = UUID.randomUUID().toString(),
                    firstName = formData["firstName"]?.get(0)!!,
                    lastName = formData["lastName"]?.get(0)!!,
                    specialities = formData["specialities"]?.toCollection(HashSet())!!))
        }.then(goToIndex())
    }

    fun goToEdit(serverRequest: ServerRequest): Mono<ServerResponse> {
        return vetRepository.findById(serverRequest.queryParam("id").orElseThrow({IllegalArgumentException()})).map {
            mapOf("vet" to it, "specialities" to specialityRepository.findAll())
        }.flatMap { ok().html().render("vets/edit", it) }
    }

    fun edit(serverRequest: ServerRequest): Mono<ServerResponse> {
        return serverRequest.body(BodyExtractors.toFormData()).flatMap {
            formData ->
            vetRepository.save(Vet(
                    id = formData["id"]?.get(0)!!,
                    firstName = formData["firstName"]?.get(0)!!,
                    lastName = formData["lastName"]?.get(0)!!,
                    specialities = formData["specialities"]?.toCollection(HashSet<String>())!!))
        }.then(goToIndex())
    }

    fun delete(serverRequest: ServerRequest): Mono<ServerResponse> {
        return goToIndex()
    }

    private fun goToIndex(): Mono<ServerResponse> = ok().html()
            .render("vets/index", mapOf("vets" to vetRepository.findAll()))

}