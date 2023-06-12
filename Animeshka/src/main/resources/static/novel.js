
function removeNovelRelation(id) {
    document.getElementById('novel_relations').removeChild(document.getElementById(id))
}


async function searchNovel(containerID){
    let searchTitle = $(`#novel_relation_search_${containerID}`).val()
    let response = await fetch("/novel/title?"+ new URLSearchParams({ title : searchTitle }),{ method : "GET",credentials: "same-origin" })

    if(response.status !== 200){
        addAlert($(`#novel_relation_${containerID}`).children(".alerts"),await response.text())
        $(`#show_result_novel_relation_${containerID}`).removeClass("d-block").addClass("d-none")
        return
    }

    let result = JSON.parse(await response.text())

    $(`#show_result_novel_relation_${containerID}`).removeClass("d-none").addClass("d-block")
    $(`#novel_relation_offcanvas_image_${containerID}`).attr("src",result.posterPath)
    $(`#novel_relation_offcanvas_title_${containerID}`).text(`${result.title}/${result.japaneseTitle}`)
    $(`#novel_relation_offcanvas_type_${containerID}`).val(result.novelType.split("_").map(value => value.toLowerCase()).join(" "))
    $(`#novel_relation_offcanvas_demographics_${containerID}`).val(result.demographic.toLowerCase())
    $(`#novel_relation_offcanvas_status_${containerID}`).val(result.novelStatus.split("_").map(value => value.toLowerCase()).join(" "))
    $(`#novel_relation_offcanvas_published_${containerID}`).val(result.published)
    $(`#novel_relation_offcanvas_finished_${containerID}`).val(result.finished)
    $(`#novel_relation_offcanvas_synopsis_${containerID}`).text(result.synopsis)
    $(`#novel_relation_offcanvas_background_${containerID}`).text(result.background)
    $(`#novel_relation_offcanvas_details_${containerID}`).attr("href",`/novel/${result.id}`)
}

function addAlert(alertContainer,text){
    alertContainer.append($("<div>").attr("class","alert alert-danger alert-dismissible fade show mt-2").attr("role","alert").text(text).append($("<button>").attr("class","btn-close").attr("type","button").attr("data-bs-dismiss","alert").attr("aria-label","Close")))
}

function addNovelRelation(id,messages){

    let removeBtnContainer = $("<div>")
        .attr("class","d-flex justify-content-end")
        .attr("for",``)
        .append($("<button>").attr("class","btn btn-danger").attr("type","button").text(messages.removeBtnText).on("click",function(){ removeNovelRelation(`novel_relation_${id}`) }))

    let alertsContainer = $("<div class='alerts'>")

    let relationSelectLabel = $("<label>")
        .attr("class","form-label")
        .attr("for",`novel_relation_select_${id}`)
        .text(messages.relationSelectLabelText)

    let relationSelect = $("<select>")
        .attr("class","form-control")
        .attr("id",`novel_relation_select_${id}`)

    for (let i = 0; i < messages.relationOptions.length; i++) {
        $("<option>").text(messages.relationOptions[i].toLowerCase().split("_").join(" ")).appendTo(relationSelect)
    }

    let searchLabel = $("<label>")
        .attr("class","form-label")
        .attr("for",`novel_relation_search_${id}`)
        .text(messages.novelSearchLabelText)

    let searchContainer = $("<div>")
        .attr("class","d-flex")
        .attr("role","search")
        .append($("<input>").attr("class","form-control me-2").attr("id",`novel_relation_search_${id}`).attr("type","search"))
        .append($("<button>").attr("class","btn btn-outline-success").attr("type","button").text(messages.searchBtnText).on("click",function(){ searchNovel(id) }))

    let showResultButton = $("<button>")
        .attr("class","btn btn-secondary mt-2 w-100 d-none")
        .attr("type","button")
        .attr("data-bs-toggle","offcanvas")
        .attr("data-bs-target",`#novel_relation_offcanvas_${id}`)
        .attr("id",`show_result_novel_relation_${id}`)
        .text(messages.showResultBtnText)

    let offcanvasContainer = $("<div>")
        .attr("class","offcanvas offcanvas-start")
        .attr("id",`novel_relation_offcanvas_${id}`)
        .append(
            $("<div>").attr("class","offcanvas-header")
                .append($("<h5>").attr("class","offcanvas-title w-100 text-center").attr("id",`novel_relation_offcanvas_title_${id}`))
                .append($("<button>").attr("class","btn-close").attr("type","button").attr("data-bs-dismiss","offcanvas").attr("aria-label","Close"))
        ).append(
            $("<div>")
                .attr("class","offcanvas-body")
                .append($("<img>").attr("class","img-fluid m-auto d-block").attr("id",`novel_relation_offcanvas_image_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_type_${id}`).text(messages.novelRelationTypeLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_type_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_demographics_${id}`).text(messages.novelRelationDemographicsLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_demographics_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_novel_status_${id}`).text(messages.novelRelationStatusLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_status_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_published_${id}`).text(messages.novelRelationPublishedLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_published_${id}`).attr("type","date"))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_finished_${id}`).text(messages.novelRelationFinishedLabelText))
                .append($("<input>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_finished_${id}`).attr("type","date"))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_synopsis_${id}`).text(messages.novelRelationSynopsisLabelText))
                .append($("<textarea>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_synopsis_${id}`))
                .append($("<label>").attr("class","form-label").attr("for",`novel_relation_offcanvas_background_${id}`).text(messages.novelRelationBackgroundLabelText))
                .append($("<textarea>").attr("class","form-control").attr("readonly","readonly").attr("id",`novel_relation_offcanvas_background_${id}`))
                .append($("<a>").attr("class","btn btn-primary mt-2 w-100").attr("target","_blank").attr("id",`novel_relation_offcanvas_details_${id}`).text(messages.novelRelationMoreDetailsBtnText))
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
        .append(showResultButton)
        .append(offcanvasContainer)
        .insertBefore("#add_novel_relation_btn")
}