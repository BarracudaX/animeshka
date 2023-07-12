function removeAnimeCharacter(id){
    document.getElementById("characters").removeChild(document.getElementById(id))
}

async function searchVoiceActor(containerID,pageNumber = 0,isPrevBtnSource = false,pageSize = 2){
    let searchInput = $(`#character_relation_voice_actor_search_${containerID}`).val()
    let response = await fetch("/person/search?"+ new URLSearchParams({ searchKey : searchInput,page : `${pageNumber}`,size : `${pageSize}` }),{ method : "GET",credentials: "same-origin" })

    if(response.status !== 200){
        addAlert($(`#character_relation_${containerID}`).children(".alerts"),await response.text())
        $(`#voice_actor_relation_choose_btn_${containerID}`).removeClass("d-block").addClass("d-none")
        return
    }

    let page = JSON.parse(await response.text())

    //0 search hits.
    if(page.content.length === 0){
        $(`#voice_actor_relation_non_found_alert_${containerID}`).removeClass("d-none").addClass("d-block")
        return
    }

    let currentVoiceActorSelectedPosition = 0
    let previousBtn = $(`#voice_actor_relation_offcanvas_previous_btn_${containerID}`)
    let nextBtn = $(`#voice_actor_relation_offcanvas_next_btn_${containerID}`)
    //remove any previously registered click events.
    previousBtn.off("click")
    nextBtn.off("click")

    /**
     * Go to the last voice actor of the search request since request was initiated by user clicking on previous btn.
     */
    if(isPrevBtnSource){
        currentVoiceActorSelectedPosition = page.content.length - 1
    }

    if(!page.hasNext && page.content.length === 1){
        nextBtn.addClass("disabled")
    }else{
        nextBtn.removeClass("disabled")
    }

    if(currentVoiceActorSelectedPosition === 0 && !page.hasPrevious){
        previousBtn.addClass("disabled")
    }else{
        previousBtn.removeClass("disabled")
    }


    nextBtn.on("click",function (){
        if(currentVoiceActorSelectedPosition === page.content.length - 1 && page.hasNext){
            searchVoiceActor(containerID,pageNumber + 1)
            return
        }
        currentVoiceActorSelectedPosition = currentVoiceActorSelectedPosition + 1
        if(currentVoiceActorSelectedPosition === page.content.length - 1 && !page.hasNext){
            nextBtn.addClass("disabled")
        }
        setOffcanvasVoiceActorDetails(containerID,page.content[currentVoiceActorSelectedPosition])
        previousBtn.removeClass("disabled")
    })

    previousBtn.on("click",function() {
        if(currentVoiceActorSelectedPosition === 0 && page.hasPrevious){
            searchVoiceActor(containerID,pageNumber - 1,true)
            return
        }
        currentVoiceActorSelectedPosition = currentVoiceActorSelectedPosition - 1
        if(currentVoiceActorSelectedPosition === 0 && !page.hasPrevious){
            previousBtn.addClass("disabled")
        }

        setOffcanvasVoiceActorDetails(containerID,page.content[currentVoiceActorSelectedPosition])
        nextBtn.removeClass("disabled")
    })

    setOffcanvasVoiceActorDetails(containerID,page.content[currentVoiceActorSelectedPosition])
}

function setOffcanvasVoiceActorDetails(containerID,content){
    let title = `${content.firstName} ${content.lastName}/${content.familyName || ''} ${content.givenName || ''}`
    $(`#voice_actor_relation_non_found_alert_${containerID}`).addClass("d-none").removeClass("d-block")
    $(`#voice_actor_relation_choose_btn_${containerID}`).removeClass("d-none").addClass("d-block").text(title)
    $(`#voice_actor_relation_offcanvas_image_${containerID}`).attr("src",`/image/${content.posterID}`)
    $(`#voice_actor_relation_offcanvas_title_${containerID}`).text(title)
    $(`#voice_actor_relation_offcanvas_birthdate_${containerID}`).val(content.birthDate)
    $(`#voice_actor_relation_offcanvas_description_${containerID}`).text(content.description)
    $(`#voice_actor_relation_offcanvas_details_${containerID}`).attr("href",`/person/${content.id}`)
    $(`#character_relation_va_hidden_${containerID}`).val(content.id)
    $(`#character_relation_va_hidden_${containerID}`).trigger("input") //trigger for validation to kick off
}


async function searchCharacter(containerID,pageNumber = 0,isPrevBtnSource = false,pageSize = 2){
    let searchInput = $(`#character_relation_character_search_${containerID}`).val()
    let response = await fetch("/character/search?"+ new URLSearchParams({ searchKey : searchInput,page : `${pageNumber}`,size : `${pageSize}` }),{ method : "GET",credentials: "same-origin" })

    if(response.status !== 200){
        addAlert($(`#character_relation_${containerID}`).children(".alerts"),await response.text())
        $(`#character_relation_choose_btn_${containerID}`).removeClass("d-block").addClass("d-none")
        return
    }

    let page = JSON.parse(await response.text())

    //0 search hits.
    if(page.content.length === 0){
        $(`#character_relation_non_found_alert_${containerID}`).removeClass("d-none").addClass("d-block")
        return
    }


    let currentCharacterSelectedPosition = 0
    let previousBtn = $(`#character_relation_offcanvas_previous_btn_${containerID}`)
    let nextBtn = $(`#character_relation_offcanvas_next_btn_${containerID}`)
    //remove any previously registered click events.
    previousBtn.off("click")
    nextBtn.off("click")

    /**
     * Go to the last character of the search request since request was initiated by user clicking on previous btn.
     */
    if(isPrevBtnSource){
        currentCharacterSelectedPosition = page.content.length - 1
    }

    if(!page.hasNext && page.content.length === 1){
        nextBtn.addClass("disabled")
    }else{
        nextBtn.removeClass("disabled")
    }

    if(currentCharacterSelectedPosition === 0 && !page.hasPrevious){
        previousBtn.addClass("disabled")
    }else{
        previousBtn.removeClass("disabled")
    }


    nextBtn.on("click",function (){
        if(currentCharacterSelectedPosition === page.content.length - 1 && page.hasNext){
            searchCharacter(containerID,pageNumber + 1)
            return
        }
        currentCharacterSelectedPosition = currentCharacterSelectedPosition + 1
        if(currentCharacterSelectedPosition === page.content.length - 1 && !page.hasNext){
            nextBtn.addClass("disabled")
        }
        setOffcanvasCharacterDetails(containerID,page.content[currentCharacterSelectedPosition])
        previousBtn.removeClass("disabled")
    })

    previousBtn.on("click",function() {
        if(currentCharacterSelectedPosition === 0 && page.hasPrevious){
            searchCharacter(containerID,pageNumber - 1,true)
            return
        }
        currentCharacterSelectedPosition = currentCharacterSelectedPosition - 1
        if(currentCharacterSelectedPosition === 0 && !page.hasPrevious){
            previousBtn.addClass("disabled")
        }

        setOffcanvasCharacterDetails(containerID,page.content[currentCharacterSelectedPosition])
        nextBtn.removeClass("disabled")
    })

    setOffcanvasCharacterDetails(containerID,page.content[currentCharacterSelectedPosition])

}

function setOffcanvasCharacterDetails(containerID,content){
    $(`#character_relation_non_found_alert_${containerID}`).addClass("d-none").removeClass("d-block")
    $(`#character_relation_choose_btn_${containerID}`).removeClass("d-none").addClass("d-block").text(`${content.characterName}/${content.japaneseName}`)
    $(`#character_relation_offcanvas_image_${containerID}`).attr("src",`/image/${content.posterID}`)
    $(`#character_relation_offcanvas_title_${containerID}`).text(`${content.characterName}/${content.japaneseName}`)
    $(`#character_relation_offcanvas_description_${containerID}`).text(content.description)
    $(`#character_relation_offcanvas_details_${containerID}`).attr("href",`/character/${content.id}`)
    $(`#character_relation_hidden_${containerID}`).val(content.id)
    $(`#character_relation_hidden_${containerID}`).trigger("input") //trigger for validation to kick off
}

function addAlert(alertContainer,text){
    alertContainer.append($("<div>").attr("class","alert alert-danger alert-dismissible fade show mt-2").attr("role","alert").text(text).append($("<button>").attr("class","btn-close").attr("type","button").attr("data-bs-dismiss","alert").attr("aria-label","Close")))
}


function addCharacterRelation(id){
    let characterRelationID = `character_relation_${id}`

    let removeBtnContainer = $("<div>")
        .attr("class","d-flex justify-content-end")
        .attr("for",``)
        .append($("<button>").attr("class","btn btn-danger").attr("type","button").text(removeBtnText).on("click",function(){ removeAnimeCharacter(characterRelationID) }))

    let alertsContainer = $("<div class='alerts mt-1'>")
        .append($("<div class='alert alert-warning d-none'>").text(characterNotFound).attr("id",`character_relation_non_found_alert_${id}`))
        .append($("<div class='alert alert-warning d-none'>").text(voiceActorNotFound).attr("id",`voice_actor_relation_non_found_alert_${id}`))

    let roleSelectLabel = $("<label>")
        .attr("class","form-label")
        .attr("for",`character_relation_select_${id}`)
        .text(roleSelectLabelText)

    let roleSelect = $("<select>")
        .attr("class","form-control")
        .attr("id",`character_relation_select_${id}`)

    let voiceActorLabelEle = $("<label>")
        .attr("class","form-label")
        .attr("for",`character_relation_voice_actor_search_${id}`)
        .text(voiceActorLabel)

    let searchVoiceActorContainer = $("<div>")
        .attr("class","d-flex")
        .attr("role","search")
        .append($("<input>").attr("class","form-control me-2").attr("id",`character_relation_voice_actor_search_${id}`).attr("type","search"))
        .append($("<button>").attr("class","btn btn-outline-success").attr("type","button").text(searchBtnText).on("click",function(){ searchVoiceActor(id) }))
    let hiddenVoiceActorID = $("<input type='hidden'>")
        .attr("id",`character_relation_va_hidden_${id}`)
        .attr("class","character_relation_va_hidden_id")
    hiddenVoiceActorID.on("input",function(){ isVoiceActorRelationValid(hiddenVoiceActorID) })

    for (let i = 0; i < roleOptions.length; i++) {
        $("<option>").text(roleOptions[i].toLowerCase().split("_").join(" ")).val(roleOptions[i].toUpperCase()).appendTo(roleSelect)
    }

    let searchLabel = $("<label>")
        .attr("class","form-label")
        .attr("for",`character_relation_character_search_${id}`)
        .text(characterSearchLabelText)

    let searchCharacterContainer = $("<div>")
        .attr("class","d-flex")
        .attr("role","search")
        .append($("<input>").attr("class","form-control me-2").attr("id",`character_relation_character_search_${id}`).attr("type","search"))
        .append($("<button>").attr("class","btn btn-outline-success").attr("type","button").text(searchBtnText).on("click",function(){ searchCharacter(id) }))
    let hiddenCharacterID = $("<input type='hidden'>")
        .attr("id",`character_relation_hidden_${id}`)
        .attr("class","character_relation_hidden_id")
    hiddenCharacterID.on("input",function(){ isCharacterRelationValid(hiddenCharacterID) })

    let chooseCharacterRelationBtn = $("<button>")
        .attr("class","btn btn-secondary mt-2 w-100 d-none")
        .attr("type","button")
        .attr("data-bs-toggle","offcanvas")
        .attr("data-bs-target",`#character_relation_offcanvas_${id}`)
        .attr("id",`character_relation_choose_btn_${id}`)

    let chooseVoiceActorRelationBt = $("<button>")
        .attr("class","btn btn-secondary mt-2 w-100 d-none")
        .attr("type","button")
        .attr("data-bs-toggle","offcanvas")
        .attr("data-bs-target",`#voice_actor_relation_offcanvas_${id}`)
        .attr("id",`voice_actor_relation_choose_btn_${id}`)

    let characterOffcanvasContainer = $("<div>")
        .attr("class","offcanvas offcanvas-start w-50")
        .attr("id",`character_relation_offcanvas_${id}`)
        .append(
            $("<div>").attr("class","offcanvas-header")
                .append($("<h5>").attr("class","offcanvas-title w-100 text-center").attr("id",`character_relation_offcanvas_title_${id}`))
                .append($("<button>").attr("class","btn-close").attr("type","button").attr("data-bs-dismiss","offcanvas").attr("aria-label","Close"))
        ).append(
            $("<div>")
                .attr("class","offcanvas-body")
                .append($("<img>").attr("class","img-fluid m-auto d-block").attr("id",`character_relation_offcanvas_image_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`character_relation_offcanvas_description_${id}`).text(descriptionLabel))
                .append($("<textarea>").attr("class","form-control").attr("readonly","readonly").attr("id",`character_relation_offcanvas_description_${id}`))
                .append($("<a>").attr("class","btn btn-primary mt-2 w-100").attr("target","_blank").attr("id",`character_relation_offcanvas_details_${id}`).text(moreDetailsLabel))
                .append(
                    $("<div>").attr("class","d-flex mt-1")
                        .append($("<button>").attr("class","btn btn-primary").attr("type","button").attr("id",`character_relation_offcanvas_previous_btn_${id}`).text(previousBtnText))
                        .append($("<button>").attr("class","btn btn-primary ms-auto").attr("type","button").attr("id",`character_relation_offcanvas_next_btn_${id}`).text(nextBtnText))
                )
        )


    let voiceActorOffcanvasContainer = $("<div>")
        .attr("class","offcanvas offcanvas-start w-50")
        .attr("id",`voice_actor_relation_offcanvas_${id}`)
        .append(
            $("<div>").attr("class","offcanvas-header")
                .append($("<h5>").attr("class","offcanvas-title w-100 text-center").attr("id",`voice_actor_relation_offcanvas_title_${id}`))
                .append($("<button>").attr("class","btn-close").attr("type","button").attr("data-bs-dismiss","offcanvas").attr("aria-label","Close"))
        ).append(
            $("<div>")
                .attr("class","offcanvas-body")
                .append($("<img>").attr("class","img-fluid m-auto d-block").attr("id",`voice_actor_relation_offcanvas_image_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`voice_actor_relation_offcanvas_birthdate_${id}`).text(birthDateLabel))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("type","date").attr("id",`voice_actor_relation_offcanvas_birthdate_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`voice_actor_relation_offcanvas_description_${id}`).text(descriptionLabel))
                .append($("<textarea>").attr("class","form-control").attr("readonly","readonly").attr("id",`voice_actor_relation_offcanvas_description_${id}`))
                .append($("<a>").attr("class","btn btn-primary mt-2 w-100").attr("target","_blank").attr("id",`voice_actor_relation_offcanvas_details_${id}`).text(moreDetailsLabel))
                .append(
                    $("<div>").attr("class","d-flex mt-1")
                        .append($("<button>").attr("class","btn btn-primary").attr("type","button").attr("id",`voice_actor_relation_offcanvas_previous_btn_${id}`).text(previousBtnText))
                        .append($("<button>").attr("class","btn btn-primary ms-auto").attr("type","button").attr("id",`voice_actor_relation_offcanvas_next_btn_${id}`).text(nextBtnText))
                )
        )
    $("<div>")
        .attr("class","border border-secondary-subtle container-fluid p-2 mt-2 character_relation")
        .attr("id",`character_relation_${id}`)
        .append(removeBtnContainer)
        .append(alertsContainer)
        .append(roleSelectLabel)
        .append(roleSelect)
        .append(searchLabel)
        .append(searchCharacterContainer)
        .append(hiddenCharacterID)
        .append(chooseCharacterRelationBtn)
        .append(voiceActorLabelEle)
        .append(searchVoiceActorContainer)
        .append(hiddenVoiceActorID)
        .append(chooseVoiceActorRelationBt)
        .append(characterOffcanvasContainer)
        .append(voiceActorOffcanvasContainer)
        .insertBefore("#add_character_relation_btn")
}