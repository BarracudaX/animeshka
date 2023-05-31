

function removeNovelRelation(id) {
    document.getElementById('novel_relations').removeChild(document.getElementById(id))
}

function removeAnimeRelation(id){
    document.getElementById("anime_relations").removeChild(document.getElementById(id))
}

function removeAnimeCharacter(id){
    document.getElementById("characters").removeChild(document.getElementById(id))
}

async function searchNovel(containerID){
    let searchTitle = $(`#novel_relation_search_${containerID}`).val()
    let response = await fetch("/novel/title?"+ new URLSearchParams({ title : searchTitle }),{ method : "GET",credentials: "same-origin" })

    if(response.status !== 200){
        addAlert($(`#novel_relation_${containerID}`).children(".alerts"),await response.text())
        return
    }

    let result = JSON.parse(await response.text())

    console.log(result)

}

function addAlert(alertContainer,text){
    alertContainer.append($("<div>").attr("class","alert alert-danger alert-dismissible fade show mt-2").attr("role","alert").text(text).append($("<button>").attr("class","btn-close").attr("type","button").attr("data-bs-dismiss","alert").attr("aria-label","Close")))
}