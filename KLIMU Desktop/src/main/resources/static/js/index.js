let errCount = 0

function doGet() {
    $.ajax({
        url: "http://localhost:4040/klimu/notification",
        type: 'GET',
        dataType: 'json',
        success: function (response) {
            let display = $("#alert_display")
            let cardParent = $(".alerts-div")

            // Set the error count to 0.
            errCount = 0

            if (response.length > 0) {
                if (display.hasClass('visually-hidden')) {
                    display.removeClass('visually-hidden')
                }
                display.text('You have ' + response.length +  ' new notifications!')

                $.each(response, function (index, alert) {
                    let html = ""

                    html += '<div class="card m-3">'
                    html += '<div class="card-header row ms-0 me-0">'
                    html += '<div class="col">' + alert.type.name + ' at ' + alert.location.city + ', ' + alert.location.country +  '</div>'
                    html += '<div class="col" style="text-align: right">'
                    switch (alert.type.type) {
                        case 'Information':
                            html += '<span class="m-3"><i class="bi bi-info-circle"></i></span>'
                            break;
                        case 'Warning':
                            html += '<span class="m-3"><i class="bi bi-exclamation-triangle"></i></span>'
                            break;
                        case 'Dangerous':
                            html += '<span class="m-3"><i class="bi bi-exclamation-square"></i></span>'
                            break;
                    }
                    html += '</div>'
                    html += '</div>'
                    html += '<div class="card-body">'
                    html += '<h3>' + alert.date + '</h3>'
                    html += '<p>' + alert.message + '</p>'
                    html += '</div>'
                    html += '</div>'

                    cardParent.prepend(html)
                })
            } else {
                console.log("No new notifications")
            }
        },
        error: function (error) {
            console.log("The application is not running")
            errCount++
        }
    })
    if (errCount < 5) {
        setTimeout(doGet, 1000 * 5)
    }
}
$(document).ready(function () {
    console.log("Loaded")
    setTimeout(doGet, 1000 * 5)
})