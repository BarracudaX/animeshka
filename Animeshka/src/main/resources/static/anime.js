
function removeAnimeRelation(id){
    document.getElementById("anime_relations").removeChild(document.getElementById(id))
}

/**
 *
 * @param containerID - used to get the user's input and to present the result of the search.
 * @param pageNumber - the number of the page that will be requested. Default - 0.
 * @param isPrevBtnSource - whether the request was triggered by user clicking the previous btn. If true, instead of showing the first item of the page, user will be presented with the last item of the page.
 */
async function searchAnime(containerID,pageNumber = 0,isPrevBtnSource = false,pageSize = 2){
    let searchKey = $(`#anime_relation_search_${containerID}`).val()
    let response = await fetch("/anime/search?"+ new URLSearchParams({ searchKey : searchKey, page : `${pageNumber}` , size : `${pageSize}`}),{ method : "GET",credentials: "same-origin" })

    if(response.status !== 200){
        addAlert($(`#anime_relation_${containerID}`).children(".alerts"),await response.text())
        $(`#anime_relation_choose_btn_${containerID}`).removeClass("d-block").addClass("d-none")
        return
    }

    let page = JSON.parse(await response.text())

    //0 search hits.
    if(page.content.length === 0){
        $(`#anime_relation_non_found_alert_${containerID}`).removeClass("d-none").addClass("d-block")
        return
    }

    let currentAnimeSelectedPosition = 0
    let previousBtn = $(`#anime_relation_offcanvas_previous_btn_${containerID}`)
    let nextBtn = $(`#anime_relation_offcanvas_next_btn_${containerID}`)
    //remove any previously registered click events.
    previousBtn.off("click")
    nextBtn.off("click")

    /**
     * Go to the last anime of the search request since request was initiated by user clicking on previous btn.
     */
    if(isPrevBtnSource){
        currentAnimeSelectedPosition = page.content.length - 1
    }

    if(!page.hasNext && page.content.length === 1){
        nextBtn.addClass("disabled")
    }else{
        nextBtn.removeClass("disabled")
    }

    if(currentAnimeSelectedPosition === 0 && !page.hasPrevious){
        previousBtn.addClass("disabled")
    }else{
        previousBtn.removeClass("disabled")
    }

    nextBtn.on("click",function (){
        if(currentAnimeSelectedPosition === page.content.length - 1 && page.hasNext){
            searchAnime(containerID,pageNumber + 1)
            return
        }
        currentAnimeSelectedPosition = currentAnimeSelectedPosition + 1
        if(currentAnimeSelectedPosition === page.content.length - 1 && !page.hasNext){
            nextBtn.addClass("disabled")
        }
        setOffcanvasAnimeDetails(containerID,page.content[currentAnimeSelectedPosition])
        previousBtn.removeClass("disabled")
    })

    previousBtn.on("click",function() {
        if(currentAnimeSelectedPosition === 0 && page.hasPrevious){
            searchAnime(containerID,pageNumber - 1,true)
            return
        }
        currentAnimeSelectedPosition = currentAnimeSelectedPosition - 1
        if(currentAnimeSelectedPosition === 0 && !page.hasPrevious){
            previousBtn.addClass("disabled")
        }

        setOffcanvasAnimeDetails(containerID,page.content[currentAnimeSelectedPosition])
        nextBtn.removeClass("disabled")
    })

    setOffcanvasAnimeDetails(containerID,page.content[currentAnimeSelectedPosition])
}

function setOffcanvasAnimeDetails(containerID,content){
    $(`#anime_relation_non_found_alert_${containerID}`).addClass("d-none").removeClass("d-block")
    $(`#anime_relation_choose_btn_${containerID}`).removeClass("d-none").addClass("d-block").text(`${content.title}/${content.japaneseTitle}`)
    $(`#anime_relation_offcanvas_image_${containerID}`).attr("src",`/image/${content.posterID}`)
    $(`#anime_relation_offcanvas_title_${containerID}`).text(`${content.title}/${content.japaneseTitle}`)
    $(`#anime_relation_offcanvas_type_${containerID}`).val(content.animeType.split("_").map(value => value.toLowerCase()).join(" "))
    $(`#anime_relation_offcanvas_demographics_${containerID}`).val(content.demographic.toLowerCase())
    $(`#anime_relation_offcanvas_status_${containerID}`).val(content.status.split("_").map(value => value.toLowerCase()).join(" "))
    $(`#anime_relation_offcanvas_published_${containerID}`).val(content.airedAt)
    $(`#anime_relation_offcanvas_finished_${containerID}`).val(content.finishedAt)
    $(`#anime_relation_offcanvas_synopsis_${containerID}`).text(content.synopsis)
    $(`#anime_relation_offcanvas_background_${containerID}`).text(content.background)
    $(`#anime_relation_offcanvas_details_${containerID}`).attr("href",`/anime/${content.id}`)
    $(`#anime_relation_hidden_${containerID}`).val(content.id)
}

function addAlert(alertContainer,text){
    alertContainer.append($("<div>").attr("class","alert alert-danger alert-dismissible fade show mt-2").attr("role","alert").text(text).append($("<button>").attr("class","btn-close").attr("type","button").attr("data-bs-dismiss","alert").attr("aria-label","Close")))
}

function addAnimeRelation(id,messages){
    let animeRelationID = `anime_relation_${id}`

    let removeBtnContainer = $("<div>")
        .attr("class","d-flex justify-content-end")
        .attr("for",``)
        .append($("<button>").attr("class","btn btn-danger").attr("type","button").text(removeBtnText).on("click",function(){ removeAnimeRelation(animeRelationID) }))

    let alertsContainer = $("<div class='alerts mt-1'>").append($("<div class='alert alert-warning d-none'>").text(messages.animeSearchNotFound).attr("id",`anime_relation_non_found_alert_${id}`))

    let relationSelectLabel = $("<label>")
        .attr("class","form-label")
        .attr("for",`anime_relation_select_${id}`)
        .text(messages.relationSelectLabelText)

    let relationSelect = $("<select>")
        .attr("class","form-control")
        .attr("id",`anime_relation_select_${id}`)

    for (let i = 0; i < messages.relationOptions.length; i++) {
        $("<option>").text(messages.relationOptions[i].toLowerCase().split("_").join(" ")).appendTo(relationSelect)
    }

    let searchLabel = $("<label>")
        .attr("class","form-label")
        .attr("for",`anime_relation_search_${id}`)
        .text(messages.animeSearchLabelText)

    let searchContainer = $("<div>")
        .attr("class","d-flex")
        .attr("role","search")
        .append($("<input>").attr("class","form-control me-2").attr("id",`anime_relation_search_${id}`).attr("type","search"))
        .append($("<button>").attr("class","btn btn-outline-success").attr("type","button").text(searchBtnText).on("click",function(){ searchAnime(id) }))

    let hiddenID = $("<input>")
        .attr("type","hidden")
        .attr("id",`anime_relation_hidden_${id}`)
        .attr("class","anime_relation_hidden_id")

    let chooseRelationBtn = $("<button>")
        .attr("class","btn btn-secondary mt-2 w-100 d-none")
        .attr("type","button")
        .attr("data-bs-toggle","offcanvas")
        .attr("data-bs-target",`#anime_relation_offcanvas_${id}`)
        .attr("id",`anime_relation_choose_btn_${id}`)

    let offcanvasContainer = $("<div>")
        .attr("class","offcanvas offcanvas-start w-50")
        .attr("id",`anime_relation_offcanvas_${id}`)
        .append(
            $("<div>").attr("class","offcanvas-header")
                .append($("<h5>").attr("class","offcanvas-title w-100 text-center").attr("id",`anime_relation_offcanvas_title_${id}`))
                .append($("<button>").attr("class","btn-close").attr("type","button").attr("data-bs-dismiss","offcanvas").attr("aria-label","Close"))
        ).append(
            $("<div>")
                .attr("class","offcanvas-body")
                .append($("<img>").attr("class","img-fluid m-auto d-block").attr("id",`anime_relation_offcanvas_image_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`anime_relation_offcanvas_type_${id}`).text(messages.animeRelationTypeLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`anime_relation_offcanvas_type_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`anime_relation_offcanvas_demographics_${id}`).text(messages.animeRelationDemographicsLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`anime_relation_offcanvas_demographics_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`anime_relation_offcanvas_status_${id}`).text(messages.animeRelationStatusLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`anime_relation_offcanvas_status_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`anime_relation_offcanvas_published_${id}`).text(messages.animeRelationPublishedLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`anime_relation_offcanvas_published_${id}`).attr("type","date"))
                .append($("<label>").attr("class","form-label").attr("for",`anime_relation_offcanvas_finished_${id}`).text(messages.animeRelationFinishedLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`anime_relation_offcanvas_finished_${id}`).attr("type","date"))
                .append($("<label>").attr("class","form-label").attr("for",`anime_relation_offcanvas_synopsis_${id}`).text(messages.animeRelationSynopsisLabelText))
                .append($("<textarea>").attr("class","form-control").attr("readonly","readonly").attr("id",`anime_relation_offcanvas_synopsis_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`anime_relation_offcanvas_background_${id}`).text(messages.animeRelationBackgroundLabelText))
                .append($("<textarea>").attr("class","form-control").attr("readonly","readonly").attr("id",`anime_relation_offcanvas_background_${id}`))
                .append($("<a>").attr("class","btn btn-primary mt-2 w-100").attr("target","_blank").attr("id",`anime_relation_offcanvas_details_${id}`).text(messages.animeRelationMoreDetailsBtnText))
                .append(
                    $("<div>").attr("class","d-flex mt-1")
                        .append($("<button>").attr("class","btn btn-primary").attr("type","button").attr("id",`anime_relation_offcanvas_previous_btn_${id}`).text(messages.previousBtnText))
                        .append($("<button>").attr("class","btn btn-primary ms-auto").attr("type","button").attr("id",`anime_relation_offcanvas_next_btn_${id}`).text(messages.nextBtnText))
                )
        )

    $("<div>")
        .attr("class","border border-secondary-subtle container-fluid p-2 mt-2")
        .attr("id",`anime_relation_${id}`)
        .append(removeBtnContainer)
        .append(alertsContainer)
        .append(relationSelectLabel)
        .append(relationSelect)
        .append($("<div>").attr("class","form-text").text("Relation is from this item to the specified anime. For example, sequel means that this items is sequel of searched anime."))
        .append(searchLabel)
        .append(searchContainer)
        .append(hiddenID)
        .append(chooseRelationBtn)
        .append(offcanvasContainer)
        .insertBefore("#add_anime_relation_btn")
}
