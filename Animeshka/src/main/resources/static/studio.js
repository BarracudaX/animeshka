async function searchStudio(inputContainer,isLicensorSearch = false,pageNumber = 0,isPrevBtnSource = false,pageSize = 2){
    let searchKey = $(`#${inputContainer}`).val()

    let response = await fetch("/studio/search?"+ new URLSearchParams({ searchKey : searchKey, page : `${pageNumber}` , size : `${pageSize}`}),{ method : "GET",credentials: "same-origin" })

    if(response.status !== 200){
        if(isLicensorSearch){
            addAlert($("#licensor_search_alerts"),await response.text())
            $("#licensor_choose_btn").removeClass("d-block").addClass("d-none")
        }else{
            addAlert($("#studio_search_alerts"),await response.text())
            $("#studio_choose_btn").removeClass("d-block").addClass("d-none")
        }
        return
    }
    let page = JSON.parse(await response.text())

    if(page.content.length === 0){
        if(isLicensorSearch){
            $("#licensor_not_found_alert").removeClass("d-none").addClass("d-block")
        }else{
            $("#studio_not_found_alert").removeClass("d-none").addClass("d-block")
        }
        return
    }

    let currentStudioSelectedPosition = 0
    let previousBtn,nextBtn;

    if(isLicensorSearch){
        previousBtn = $("#licensor_offcanvas_prev_btn")
        nextBtn = $("#licensor_offcanvas_next_btn")
    }else{
        previousBtn = $("#studio_offcanvas_prev_btn")
        nextBtn = $("#studio_offcanvas_next_btn")
    }

    //remove any previously registered click events.
    previousBtn.off("click")
    nextBtn.off("click")

    if(isPrevBtnSource){
        currentStudioSelectedPosition = page.content.length - 1
    }


    if(!page.hasNext && page.content.length === 1){
        nextBtn.addClass("disabled")
    }else{
        nextBtn.removeClass("disabled")
    }

    if(currentStudioSelectedPosition === 0 && !page.hasPrevious){
        previousBtn.addClass("disabled")
    }else{
        previousBtn.removeClass("disabled")
    }

    nextBtn.on("click",function (){
        if(currentStudioSelectedPosition === page.content.length - 1 && page.hasNext){
            searchStudio(inputContainer,isLicensorSearch,pageNumber + 1)
            return
        }
        currentStudioSelectedPosition = currentStudioSelectedPosition + 1
        if(currentStudioSelectedPosition === page.content.length - 1 && !page.hasNext){
            nextBtn.addClass("disabled")
        }
        if(isLicensorSearch){
            setLicensorOffcanvasDetails(page.content[currentStudioSelectedPosition])
        }else{
            setStudioOffcanvasDetails(page.content[currentStudioSelectedPosition])
        }
        previousBtn.removeClass("disabled")
    })

    previousBtn.on("click",function() {
        if(currentStudioSelectedPosition === 0 && page.hasPrevious){
            searchStudio(inputContainer,isLicensorSearch,pageNumber - 1,true)
            return
        }
        currentStudioSelectedPosition = currentStudioSelectedPosition - 1
        if(currentStudioSelectedPosition === 0 && !page.hasPrevious){
            previousBtn.addClass("disabled")
        }

        if(isLicensorSearch){
            setLicensorOffcanvasDetails(page.content[currentStudioSelectedPosition])
        }else{
            setStudioOffcanvasDetails(page.content[currentStudioSelectedPosition])
        }
        nextBtn.removeClass("disabled")
    })

    if(isLicensorSearch){
        setLicensorOffcanvasDetails(page.content[currentStudioSelectedPosition])
    }else{
        setStudioOffcanvasDetails(page.content[currentStudioSelectedPosition])
    }
}

function setStudioOffcanvasDetails(content){
    $("#studio_not_found_alert").removeClass("d-block").addClass("d-none")
    $("#studio_choose_btn").removeClass("d-none").addClass("d-block").text(`${content.studioName}/${content.japaneseName}`)
    $("#studio_offcanvas_title").text(`${content.studioName}/${content.japaneseName}`)
    $("#studio_offcanvas_image").attr("src",`/image/${content.posterID}`)
    $("#studio_offcanvas_name").val(content.studioName)
    $("#studio_offcanvas_japanese_name").val(content.japaneseName)
    $("#studio_id").val(content.id)
}

function setLicensorOffcanvasDetails(content){
    $("#licensor_not_found_alert").removeClass("d-block").addClass("d-none")
    $("#licensor_choose_btn").removeClass("d-none").addClass("d-block").text(`${content.studioName}/${content.japaneseName}`)
    $("#licensor_offcanvas_title").text(`${content.studioName}/${content.japaneseName}`)
    $("#licensor_offcanvas_image").attr("src",`/image/${content.posterID}`)
    $("#licensor_offcanvas_name").val(content.studioName)
    $("#licensor_offcanvas_japanese_name").val(content.japaneseName)
    $("#licensor_id").val(content.id)
}