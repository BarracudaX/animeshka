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

        if(!isCharacterRelationValid(characterRelationID)){
            isValid = false
            navigateTo = characterRelations[i].id
        }

        if(!isVoiceActorRelationValid(voiceActorRelationID)){
            isValid = false
            navigateTo = characterRelations[i].id
        }
    }

    let animeRelations = $(".anime_relation")
    for (let i = animeRelations.length - 1; i >= 0; i--) {
        let animeRelationID = $(animeRelations[i]).find(".anime_relation_hidden_id")
        if(!isAnimeRelationValid(animeRelationID)){
            isValid = false
            navigateTo = animeRelations[i].id
        }
    }

    let novelRelations = $(".novel_relation")
    for (let i = novelRelations.length - 1; i >= 0; i--) {
        let novelRelationID = $(novelRelations[i]).find(".novel_relation_hidden_id")
        if(!isNovelRelationValid(novelRelationID)){
            isValid = false
            navigateTo = novelRelations[i].id
        }
    }


    if(!isAnimeSynopsisValid()){
        isValid = false
        navigateTo = $("#synopsis").get(0).id
    }

    if(!isAnimePosterValid()){
        isValid = false
        navigateTo = $("#poster").get(0).id
    }

    if(!isAnimeLicensorValid()){
        isValid = false
        navigateTo = "licensor_search_input"
    }


    if(!isAnimeStudioValid()){
        isValid = false
        navigateTo = "studio_search_input"
    }

    if(!isAnimeJapaneseTitleValid()){
        isValid = false
        navigateTo = $("#japanese_title").get(0).id
    }

    if(!isAnimeTitleValid()){
        isValid = false
        navigateTo = $("#title").get(0).id
    }

    if(!isValid){
        $("html").animate({
            scrollTop : $(`#${navigateTo}`).offset().top
        },100)
    }

    return isValid
}

function isVoiceActorRelationValid(voiceActorRelationID){
    let isValid = true
    if(v8n().empty().test(voiceActorRelationID.val())){
        isValid = false
        $("<div>").attr("class","alert alert-danger form-text error").text(voiceActorRequired).insertAfter(voiceActorRelationID)
    }else{
        $(voiceActorRelationID).next(".error").remove()
    }
    return isValid
}

function isCharacterRelationValid(characterRelationID){
    let isValid = true
    if(v8n().empty().test(characterRelationID.val())){
        isValid = false
        $("<div>").attr("class","alert alert-danger form-text error").text(characterRequiredMessage).insertAfter(characterRelationID)
    }else{
        $(characterRelationID).next(".error").remove()
    }

    return isValid
}

function isAnimeRelationValid(animeRelationID){
    let isValid = true
    if(v8n().empty().test(animeRelationID.val())){
        isValid = false
        $("<div>").attr("class","alert alert-danger form-text error").text(animeRequiredMessage).insertAfter(animeRelationID)
    }else{
        $(animeRelationID).next(".error").remove()
    }

    return isValid
}

function isNovelRelationValid(novelRelationID){
    let isValid = true
    if(v8n().empty().test(novelRelationID.val())){
        isValid = false
        $("<div>").attr("class","alert alert-danger form-text error").text(animeRequiredMessage).insertAfter(novelRelationID)
    }else{
        $(novelRelationID).next(".error").remove()
    }

    return isValid
}

function isAnimeSynopsisValid(){
    let isValid = true
    let synopsis = $("#synopsis")
    if(v8n().empty().test(synopsis.val())){
        isValid = false
        $("<div>").attr("class","alert alert-danger form-text error").text(synopsisRequiredMessage).insertAfter(synopsis)
    }else{
        $(synopsis).next(".error").remove()
    }

    return isValid
}

function isAnimePosterValid(){
    let isValid = true
    let poster = $("#poster")

    if(v8n().not.exact(1).test(poster.get(0).files.length)){
        isValid = false
        $("<div>").attr("class","alert alert-danger form-text error").text(posterRequiredMessage).insertAfter(poster)
    }else{
        $(poster).next(".error").remove()
    }

    return isValid
}

function isAnimeLicensorValid(){
    let isValid = true
    let licensorID = $("#licensor_id")
    if(v8n().empty().test(licensorID.val())){
        isValid = false
        $("<div>").attr("class","alert alert-danger form-text error").text(licensorRequiredMessage).insertAfter(licensorID)
    }else{
        $(licensorID).next(".error").remove()
    }

    return isValid
}

function isAnimeStudioValid(){
    let isValid = true
    let studioID = $("#studio_id")
    if(v8n().empty().test(studioID.val())){
        isValid = false
        $("<div>").attr("class","alert alert-danger form-text error").text(studioRequiredMessage).insertAfter(studioID)
    }else{
        $(studioID).next(".error").remove()
    }

    return isValid
}

function isAnimeJapaneseTitleValid(){
    let isValid = true
    let japaneseTitle = $("#japanese_title")
    if(v8n().empty().test(japaneseTitle.val())){
        isValid = false
        $("<div>").attr("class","alert alert-danger form-text error").text(japaneseTitleRequiredMessage).insertAfter(japaneseTitle)
    }else{
        $(japaneseTitle).next(".error").remove()
    }

    return isValid
}

function isAnimeTitleValid(){
    let isValid = true
    let title = $("#title")
    if (v8n().empty().test(title.val())) {
        isValid = false
        $("<div>").attr("class","alert alert-danger form-text error").text(titleRequiredMessage).insertAfter(title)
    }else{
        $(title).next(".error").remove()
    }

    return isValid
}