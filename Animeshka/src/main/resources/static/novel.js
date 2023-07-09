
function removeNovelRelation(id) {
    document.getElementById('novel_relations').removeChild(document.getElementById(id))
}


async function searchNovel(containerID,pageNumber = 0,isPrevBtnSource = false,pageSize = 2){
    let searchKey = $(`#novel_relation_search_${containerID}`).val()
    let response = await fetch("/novel/search?"+ new URLSearchParams({ searchKey : searchKey,page : `${pageNumber}`, size : `${pageSize}` }),{ method : "GET",credentials: "same-origin" })

    if(response.status !== 200){
        addAlert($(`#novel_relation_${containerID}`).children(".alerts"),await response.text())
        $(`#novel_relation_choose_btn_${containerID}`).removeClass("d-block").addClass("d-none")
        return
    }

    let page = JSON.parse(await response.text())


    //0 search hits.
    if(page.content.length === 0){
        $(`#novel_relation_non_found_alert_${containerID}`).removeClass("d-none").addClass("d-block")
        return
    }

    let currentNovelSelectedPosition = 0
    let previousBtn = $(`#novel_relation_offcanvas_previous_btn_${containerID}`)
    let nextBtn = $(`#novel_relation_offcanvas_next_btn_${containerID}`)
    //remove any previously registered click events.
    previousBtn.off("click")
    nextBtn.off("click")

    /**
     * Go to the last novel of the search request since request was initiated by user clicking on previous btn.
     */
    if(isPrevBtnSource){
        currentNovelSelectedPosition = page.content.length - 1
    }

    if(!page.hasNext && page.content.length === 1){
        nextBtn.addClass("disabled")
    }else{
        nextBtn.removeClass("disabled")
    }

    if(currentNovelSelectedPosition === 0 && !page.hasPrevious){
        previousBtn.addClass("disabled")
    }else{
        previousBtn.removeClass("disabled")
    }

    nextBtn.on("click",function (){
        if(currentNovelSelectedPosition === page.content.length - 1 && page.hasNext){
            searchNovel(containerID,pageNumber + 1)
            return
        }
        currentNovelSelectedPosition = currentNovelSelectedPosition + 1
        if(currentNovelSelectedPosition === page.content.length - 1 && !page.hasNext){
            nextBtn.addClass("disabled")
        }
        setOffcanvasNovelDetails(containerID,page.content[currentNovelSelectedPosition])
        previousBtn.removeClass("disabled")
    })

    previousBtn.on("click",function() {
        if(currentNovelSelectedPosition === 0 && page.hasPrevious){
            searchNovel(containerID,pageNumber - 1,true)
            return
        }
        currentNovelSelectedPosition = currentNovelSelectedPosition - 1
        if(currentNovelSelectedPosition === 0 && !page.hasPrevious){
            previousBtn.addClass("disabled")
        }

        setOffcanvasNovelDetails(containerID,page.content[currentNovelSelectedPosition])
        nextBtn.removeClass("disabled")
    })


    setOffcanvasNovelDetails(containerID,page.content[currentNovelSelectedPosition])
}

function setOffcanvasNovelDetails(containerID,content){
    $(`#novel_relation_non_found_alert_${containerID}`).addClass("d-none").removeClass("d-block")
    $(`#novel_relation_choose_btn_${containerID}`).removeClass("d-none").addClass("d-block").text(`${content.title}/${content.japaneseTitle}`)
    $(`#novel_relation_offcanvas_image_${containerID}`).attr("src",`/image/${content.posterID}`)
    $(`#novel_relation_offcanvas_title_${containerID}`).text(`${content.title}/${content.japaneseTitle}`)
    $(`#novel_relation_offcanvas_type_${containerID}`).val(content.novelType.split("_").map(value => value.toLowerCase()).join(" "))
    $(`#novel_relation_offcanvas_demographics_${containerID}`).val(content.demographic.toLowerCase())
    $(`#novel_relation_offcanvas_status_${containerID}`).val(content.novelStatus.split("_").map(value => value.toLowerCase()).join(" "))
    $(`#novel_relation_offcanvas_published_${containerID}`).val(content.published)
    $(`#novel_relation_offcanvas_finished_${containerID}`).val(content.finished)
    $(`#novel_relation_offcanvas_synopsis_${containerID}`).text(content.synopsis)
    $(`#novel_relation_offcanvas_background_${containerID}`).text(content.background)
    $(`#novel_relation_offcanvas_details_${containerID}`).attr("href",`/novel/${content.id}`).attr("target","_blank")
    $(`#novel_relation_hidden_${containerID}`).val(content.id)
}

function addAlert(alertContainer,text){
    alertContainer.append($("<div>").attr("class","alert alert-danger alert-dismissible fade show mt-2").attr("role","alert").text(text).append($("<button>").attr("class","btn-close").attr("type","button").attr("data-bs-dismiss","alert").attr("aria-label","Close")))
}

function addNovelRelation(id){

    let removeBtnContainer = $("<div>")
        .attr("class","d-flex justify-content-end")
        .attr("for",``)
        .append($("<button>").attr("class","btn btn-danger").attr("type","button").text(removeBtnText).on("click",function(){ removeNovelRelation(`novel_relation_${id}`) }))

    let alertsContainer = $("<div class='alerts mt-1'>").append($("<div class='alert alert-warning d-none'>").text(novelSearchNotFound).attr("id",`novel_relation_non_found_alert_${id}`))

    let relationSelectLabel = $("<label>")
        .attr("class","form-label")
        .attr("for",`novel_relation_select_${id}`)
        .text(relationSelectLabelText)

    let relationSelect = $("<select>")
        .attr("class","form-control")
        .attr("id",`novel_relation_select_${id}`)

    for (let i = 0; i < relationOptions.length; i++) {
        $("<option>").text(relationOptions[i].toLowerCase().split("_").join(" ")).val(relationOptions[i].toUpperCase()).appendTo(relationSelect)
    }

    let searchLabel = $("<label>")
        .attr("class","form-label")
        .attr("for",`novel_relation_search_${id}`)
        .text(novelSearchLabelText)

    let searchContainer = $("<div>")
        .attr("class","d-flex")
        .attr("role","search")
        .append($("<input>").attr("class","form-control me-2").attr("id",`novel_relation_search_${id}`).attr("type","search"))
        .append($("<button>").attr("class","btn btn-outline-success").attr("type","button").text(searchBtnText).on("click",function(){ searchNovel(id) }))

    let hiddenNovelID = $("<input type='hidden'>")
        .attr("id",`novel_relation_hidden_${id}`)
        .attr("class","novel_relation_hidden_id")

    let chooseRelationBtn = $("<button>")
        .attr("class","btn btn-secondary mt-2 w-100 d-none")
        .attr("type","button")
        .attr("data-bs-toggle","offcanvas")
        .attr("data-bs-target",`#novel_relation_offcanvas_${id}`)
        .attr("id",`novel_relation_choose_btn_${id}`)

    let offcanvasContainer = $("<div>")
        .attr("class","offcanvas offcanvas-start w-50")
        .attr("id",`novel_relation_offcanvas_${id}`)
        .append(
            $("<div>").attr("class","offcanvas-header")
                .append($("<h5>").attr("class","offcanvas-title w-100 text-center").attr("id",`novel_relation_offcanvas_title_${id}`))
                .append($("<button>").attr("class","btn-close").attr("type","button").attr("data-bs-dismiss","offcanvas").attr("aria-label","Close"))
        ).append(
            $("<div>")
                .attr("class","offcanvas-body")
                .append($("<img>").attr("class","img-fluid m-auto d-block").attr("id",`novel_relation_offcanvas_image_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_type_${id}`).text(relationSelectLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_type_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_demographics_${id}`).text(demographicsLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_demographics_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_novel_status_${id}`).text(novelRelationStatusLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_status_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_published_${id}`).text(novelRelationPublishedLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_published_${id}`).attr("type","date"))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_finished_${id}`).text(finishedOnLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_finished_${id}`).attr("type","date"))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_synopsis_${id}`).text(synopsisLabelText))
                .append($("<textarea>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_synopsis_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_background_${id}`).text(backgroundLabelText))
                .append($("<textarea>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_background_${id}`))
                .append($("<a>").attr("class","btn btn-primary mt-2 w-100").attr("target","_blank").attr("id",`novel_relation_offcanvas_details_${id}`).text(moreDetailsLabel))
                .append(
                    $("<div>").attr("class","d-flex mt-1")
                        .append($("<button>").attr("class","btn btn-primary").attr("type","button").attr("id",`novel_relation_offcanvas_previous_btn_${id}`).text(previousBtnText))
                        .append($("<button>").attr("class","btn btn-primary ms-auto").attr("type","button").attr("id",`novel_relation_offcanvas_next_btn_${id}`).text(nextBtnText))
                )
        )


    $("<div>")
        .attr("class","border border-secondary-subtle container-fluid p-2 mt-2")
        .attr("id",`novel_relation_${id}`)
        .append(removeBtnContainer)
        .append(alertsContainer)
        .append(relationSelectLabel)
        .append(relationSelect)
        .append($("<div>").attr("class","form-text").text("Relation is from this item to the specified novel. For example, sequel means that this items is sequel of searched novel."))
        .append(searchLabel)
        .append(searchContainer)
        .append(hiddenNovelID)
        .append(chooseRelationBtn)
        .append(offcanvasContainer)
        .insertBefore("#add_novel_relation_btn")
}