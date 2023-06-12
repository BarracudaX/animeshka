function removeAnimeCharacter(id){
    document.getElementById("characters").removeChild(document.getElementById(id))
}


async function searchCharacter(containerID){
    let searchTitle = $(`#character_relation_character_search_${containerID}`).val()
    let response = await fetch("/character/name?"+ new URLSearchParams({ name : searchTitle }),{ method : "GET",credentials: "same-origin" })

    if(response.status !== 200){
        addAlert($(`#character_relation_${containerID}`).children(".alerts"),await response.text())
        $(`#show_result_character_relation_${containerID}`).removeClass("d-block").addClass("d-none")
        return
    }

    let result = JSON.parse(await response.text())
    let japaneseName = result.japaneseName || ""

    $(`#show_result_character_relation_${containerID}`).removeClass("d-none").addClass("d-block")
    $(`#character_relation_offcanvas_image_${containerID}`).attr("src",result.posterPath)
    $(`#character_relation_offcanvas_title_${containerID}`).text(`${result.characterName}/${japaneseName}`)
    $(`#character_relation_offcanvas_description_${containerID}`).text(result.description)
    $(`#character_relation_offcanvas_details_${containerID}`).attr("href",`/character/${result.id}`)

}

function addAlert(alertContainer,text){
    alertContainer.append($("<div>").attr("class","alert alert-danger alert-dismissible fade show mt-2").attr("role","alert").text(text).append($("<button>").attr("class","btn-close").attr("type","button").attr("data-bs-dismiss","alert").attr("aria-label","Close")))
}


function addCharacterRelation(id,messages){
    let characterRelationID = `character_relation_${id}`

    let removeBtnContainer = $("<div>")
        .attr("class","d-flex justify-content-end")
        .attr("for",``)
        .append($("<button>").attr("class","btn btn-danger").attr("type","button").text(removeBtnText).on("click",function(){ removeAnimeCharacter(characterRelationID) }))

    let alertsContainer = $("<div class='alerts'>")

    let roleSelectLabel = $("<label>")
        .attr("class","form-label")
        .attr("for",`character_relation_select_${id}`)
        .text(messages.roleLabelText)

    let roleSelect = $("<select>")
        .attr("class","form-control")
        .attr("id",`character_relation_select_${id}`)

    let voiceActorLabel = $("<label>")
        .attr("class","form-label")
        .attr("for",`character_relation_voice_actor_search_${id}`)
        .text(messages.voiceActorLabel)

    let searchVoiceActorContainer = $("<div>")
        .attr("class","d-flex")
        .attr("role","search")
        .append($("<input>").attr("class","form-control me-2").attr("id",`character_relation_voice_actor_search_${id}`).attr("type","search"))
        .append($("<button>").attr("class","btn btn-outline-success").attr("type","button").text(messages.searchBtnText).on("click",function(){ searchVoiceActor(id) }))

    for (let i = 0; i < roleOptions.length; i++) {
        $("<option>").text(roleOptions[i].toLowerCase().split("_").join(" ")).appendTo(roleSelect)
    }

    let searchLabel = $("<label>")
        .attr("class","form-label")
        .attr("for",`character_relation_character_search_${id}`)
        .text(messages.characterSearchLabelText)

    let searchCharacterContainer = $("<div>")
        .attr("class","d-flex")
        .attr("role","search")
        .append($("<input>").attr("class","form-control me-2").attr("id",`character_relation_character_search_${id}`).attr("type","search"))
        .append($("<button>").attr("class","btn btn-outline-success").attr("type","button").text(messages.searchBtnText).on("click",function(){ searchCharacter(id) }))

    let showCharacterSearchResultBtn = $("<button>")
        .attr("class","btn btn-secondary mt-2 w-100 d-none")
        .attr("type","button")
        .attr("data-bs-toggle","offcanvas")
        .attr("data-bs-target",`#character_relation_offcanvas_${id}`)
        .attr("id",`show_result_character_relation_${id}`)
        .text(messages.showResultBtnText)

    let showVoiceActorSearchResultBtn = $("<button>")
        .attr("class","btn btn-secondary mt-2 w-100 d-none")
        .attr("type","button")
        .attr("data-bs-toggle","offcanvas")
        .attr("data-bs-target",`#character_relation_offcanvas_${id}`)
        .attr("id",`show_result_character_relation_${id}`)
        .text(messages.showResultBtnText)

    let characterOffcanvasContainer = $("<div>")
        .attr("class","offcanvas offcanvas-start")
        .attr("id",`character_relation_offcanvas_${id}`)
        .append(
            $("<div>").attr("class","offcanvas-header")
                .append($("<h5>").attr("class","offcanvas-title w-100 text-center").attr("id",`character_relation_offcanvas_title_${id}`))
                .append($("<button>").attr("class","btn-close").attr("type","button").attr("data-bs-dismiss","offcanvas").attr("aria-label","Close"))
        ).append(
            $("<div>")
                .attr("class","offcanvas-body")
                .append($("<img>").attr("class","img-fluid m-auto d-block").attr("id",`character_relation_offcanvas_image_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`character_relation_offcanvas_description_${id}`).text(messages.descriptionLabel))
                .append($("<textarea>").attr("class","form-control").attr("readonly","readonly").attr("id",`character_relation_offcanvas_description_${id}`))
                .append($("<a>").attr("class","btn btn-primary mt-2 w-100").attr("target","_blank").attr("id",`character_relation_offcanvas_details_${id}`).text(messages.moreDetailsBtnText))
        )


    let voiceActorOffcanvasContainer = $("<div>")
        .attr("class","offcanvas offcanvas-start")
        .attr("id",`voice_actor_relation_offcanvas_${id}`)
        .append(
            $("<div>").attr("class","offcanvas-header")
                .append($("<h5>").attr("class","offcanvas-title w-100 text-center").attr("id",`voice_actor_relation_offcanvas_title_${id}`))
                .append($("<button>").attr("class","btn-close").attr("type","button").attr("data-bs-dismiss","offcanvas").attr("aria-label","Close"))
        ).append(
            $("<div>")
                .attr("class","offcanvas-body")
                .append($("<img>").attr("class","img-fluid m-auto d-block").attr("id",`voice_actor_relation_offcanvas_image_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`voice_actor_relation_offcanvas_birthdate_${id}`).text(messages.birthDateLabel))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("type","date").attr("id",`voice_actor_relation_offcanvas_birthdate_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`voice_actor_relation_offcanvas_description_${id}`).text(messages.descriptionLabel))
                .append($("<textarea>").attr("class","form-control").attr("readonly","readonly").attr("id",`voice_actor_relation_offcanvas_description_${id}`))
                .append($("<a>").attr("class","btn btn-primary mt-2 w-100").attr("target","_blank").attr("id",`voic_actor_relation_offcanvas_details_${id}`).text(messages.moreDetailsBtnText))
        )
    $("<div>")
        .attr("class","border border-secondary-subtle container-fluid p-2 mt-2")
        .attr("id",`character_relation_${id}`)
        .append(removeBtnContainer)
        .append(alertsContainer)
        .append(roleSelectLabel)
        .append(roleSelect)
        .append(searchLabel)
        .append(searchCharacterContainer)
        .append(showCharacterSearchResultBtn)
        .append(voiceActorLabel)
        .append(searchVoiceActorContainer)
        .append(showVoiceActorSearchResultBtn)
        .append(characterOffcanvasContainer)
        .append(voiceActorOffcanvasContainer)
        .insertBefore("#add_character_relation_btn")
}