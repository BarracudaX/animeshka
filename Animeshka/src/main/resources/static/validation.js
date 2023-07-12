/**
 * Validates the anime insertion form and returns the result of validation.
 * Note that validation order is important for scrolling.
 * @returns true if the anime form is valid and false otherwise.
 */
function animeValidation() {
    let isValid = true
    let navigateTo = null
    $(".error").remove()

    let characterRelations = $(".character_relation")
    for (let i = characterRelations.length - 1; i >= 0; i--) {
        let characterRelationID = $(characterRelations[i]).find(".character_relation_hidden_id")
        let voiceActorRelationID = $(characterRelations[i]).find(".character_relation_va_hidden_id")

        if(v8n().empty().test(characterRelationID.val())){
            isValid = false
            navigateTo = characterRelations[i].id
            $("<div>").attr("class","alert alert-danger form-text error").text(characterRequiredMessage).insertAfter(characterRelationID)
        }

        if(v8n().empty().test(voiceActorRelationID.val())){
            isValid = false
            navigateTo = characterRelations[i].id
            $("<div>").attr("class","alert alert-danger form-text error").text(voiceActorRequired).insertAfter(voiceActorRelationID)
        }

    }

    let animeRelations = $(".anime_relation")
    for (let i = animeRelations.length - 1; i >= 0; i--) {
        let animeRelationID = $(animeRelations[i]).find(".anime_relation_hidden_id")
        if(v8n().empty().test(animeRelationID.val())){
            isValid = false
            navigateTo = animeRelations[i].id
            $("<div>").attr("class","alert alert-danger form-text error").text(animeRequiredMessage).insertAfter(animeRelationID)
        }
    }

    let novelRelations = $(".novel_relation")
    for (let i = novelRelations.length - 1; i >= 0; i--) {
        let novelRelationID = $(novelRelations[i]).find(".novel_relation_hidden_id")
        if(v8n().empty().test(novelRelationID.val())){
            isValid = false
            navigateTo = novelRelations[i].id
            $("<div>").attr("class","alert alert-danger form-text error").text(novelRequiredMessage).insertAfter(novelRelationID)
        }
    }

    let synopsis = $("#synopsis")
    if(v8n().empty().test(synopsis.val())){
        isValid = false
        navigateTo = synopsis.get(0).id
        $("<div>").attr("class","alert alert-danger form-text error").text(synopsisRequiredMessage).insertAfter(synopsis)
    }

    let poster = $("#poster")
    if(v8n().not.exact(1).test(poster.get(0).files.length)){
        isValid = false
        navigateTo = poster.get(0).id
        $("<div>").attr("class","alert alert-danger form-text error").text(posterRequiredMessage).insertAfter(poster)
    }

    let licensorID = $("#licensor_id")
    if(v8n().empty().test(licensorID.val())){
        isValid = false
        navigateTo = "licensor_search_input"
        $("<div>").attr("class","alert alert-danger form-text error").text(licensorRequiredMessage).insertAfter(licensorID)
    }

    let studioID = $("#studio_id")
    if(v8n().empty().test(studioID.val())){
        isValid = false
        navigateTo = "studio_search_input"
        $("<div>").attr("class","alert alert-danger form-text error").text(studioRequiredMessage).insertAfter(studioID)
    }

    let japaneseTitle = $("#japanese_title")
    if(v8n().empty().test(japaneseTitle.val())){
        isValid = false
        navigateTo = japaneseTitle.get(0).id
        $("<div>").attr("class","alert alert-danger form-text error").text(japaneseTitleRequiredMessage).insertAfter(japaneseTitle)
    }

    let title = $("#title")
    if (v8n().empty().test(title.val())) {
        isValid = false
        navigateTo = title.get(0).id
        $("<div>").attr("class","alert alert-danger form-text error").text(titleRequiredMessage).insertAfter(title)
    }

    if(!isValid){
        $("html").animate({
            scrollTop : $(`#${navigateTo}`).offset().top
        },100)
    }

    return isValid
}