/**
 * 
 */
$(document).ready(function() {
  		getAllDivision();
    $('#create-division').click(function(event) {
      event.preventDefault();
      createDivision();
    });
    
  });
$('#saveChangesBtn').click(function () {
    saveChanges();
});

async function createDivision() {
  var code = $('#division-code').val();
  var name = $('#division-name').val();
  var requestData = {
    code: code,
    name: name
  };
  // AJAX call
  await createNewDivision(requestData)
    .then(() => {
        $('#add-data-overlay').hide();
        Swal.fire({
            title: "Success!",
            text: "You've completely added a new division.",
            icon: "success"
        });
        getAllDivision();
        $('#division-code').val('');
        $('#division-name').val('');
    })
    .catch(error => {
        console.error('Error:', error);
        Swal.fire({
            title: "Error!",
            text: "Failed to add a new division.",
            icon: "error"
        });
    });
}

function getAllDivision() {
  var rowCount =0;
    $.ajax({
      url: `api/division/divisionList`,
        type: 'POST',
        contentType: 'application/json',
        success: function (response) {
			console.log(response)
			if (response === null || response === undefined) {
		        console.log("hellooo");
		    } else if (Array.isArray(response)) {
		        if (response.length === 0) {
		            console.log("hellooo");
		        } else {
		            console.log(response);
		            console.log("h");
		            $('#division-list').empty();

	                response.forEach(function (division) {
	                  rowCount++;
	                    $('#division-list').append(`
	                        <li class="job-list-item">
	  	                      <a class="job-link" rel="nofollow"
	  	                          href="#"></a>
	  	                      <div class="job-details-container">
	  	                          <div class="lazy-avatar company-avatar">
	  	                              <img src="/assets/icons/DAT Logo.png" />
	  	                          </div>
	  	                          <div class="job-title-company-container">
	  	                              <div class="job-role">
	  	                                  <span class="job-board-job-company">${division.code}</span>
	  	                              </div>
	  	                              <h4 class="job-title job-board-job-title">
	  	                              	${division.name}
	  	                              </h4>
	  	                              <div class="job-details job-details--mobile">
	  	                                  <div class="color-deep-blue-sea-light-40">
	  	                                  
	  	                                  </div>
	  	                              </div>
	  	                          </div>
	  	                      </div>
	  	                      <div class="job-additional-details-container">
	  	                          <div class="buttons-container">
	  	                              <button class="form-btn outlined edit-data edit-button" data-division-id="${division.id}">Edit </button>
	  	                              <button class="btn2 btn2--tertiary margin-l-12 delete-button" data-division-id="${division.id}" onclick="confirmDelete(${division.id}, '${division.name}')">Delete</button>
	  	                          </div>
	  	                          <div class="job-details">
	  	                              <div class="hide-on-desktop data-detail">
	  	                                  <a class="manage-data" href="#" data-target="data-modal-1">
	  	                                      <svg xmlns="http://www.w3.org/2000/svg" width="25" height="25" fill="currentColor" class="bi bi-three-dots" viewBox="0 0 16 16">
	  	                                          <path d="M3 9.5a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3m5 0a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3m5 0a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3"/>
	  	                                      </svg>
	  	                                  </a>	  	                               
	  	                              </div>
	  	                          </div>
	  	                      </div>
	  	                  </li>
	                    `);
	                });
	                console.log("row count =", rowCount)
		        }
		    } else if (typeof response === 'object' && Object.keys(response).length === 0) {
		       $(".division-code").val(response)
		    }
            
            document.getElementById('total-count').innerText = rowCount
        },
        error: function (error) {
            console.error('Error:', error);
        }
    });
}

async function openEditModal(divisionId) {
	searchDivision(divisionId)
        .then(response => {
            $('#edit-division-code').val(response.code);
            $('#edit-division-name').val(response.name);
            $('#divisionId').val(divisionId);
            $('#edit-data-modal').show();
            $('#edit-data-overlay').show();
        })
        .catch(error => {
            console.error('Error fetching division:', error);
            Swal.fire({
                title: "Error!",
                text: "Failed to fetch division details.",
                icon: "error"
            });
        });
}

function closeEditModal() {
	$('#edit-data-modal').hide();
    $('#edit-data-overlay').hide();
}

async function saveChanges() {
    var editedCode = $("#edit-division-code").val();
    var editedName = $("#edit-division-name").val();
    var divisionId = $('#divisionId').val();
    var requestData = {
        id: divisionId,
        code: editedCode,
        name: editedName,
    };
    
    await updateDivision(requestData)
	    .then(() => {
	       $('#edit-data-overlay').hide();
	        Swal.fire({
	            title: "Success!",
	            text: "You've completely updated division.",
	            icon: "success"
	        });
	        getAllDivision();
	    })
	    .catch(error => {
	        console.error('Error:', error);
	        Swal.fire({
	            title: "Error!",
	            text: "Failed to update division.",
	            icon: "error"
	        });
	    });
}
   
async function confirmDelete(divisionId, divisionName) {
    Swal.fire({
        title: `Are you sure you want to delete ${divisionName}?`,
        text: "This action cannot be undone!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, delete it!'
    }).then(async (result) => {
        if (result.isConfirmed) {
        	await deleteDivision(divisionId)
			    .then(() => {
			        Swal.fire(
			            'Deleted!',
			            `You've completely deleted '${divisionName}' division.`,
			            'success'
			        );
			        $(`#division-list li[data-divisionId="${divisionId}"]`).remove();
			        getAllDivision();
			    })
			    .catch(error => {
			        console.error('Error deleting division:', error);
			        Swal.fire(
			            'Error!',
			            'Failed to delete the division.',
			            'error'
			        );
			    });
        }
    });
}    

$(document).on('click', '.delete-button', function() {
    var divisionId = $(this).data('divisionId');
  	var divisionName = $(this).closest('.job-list-item').find('.job-board-job-title').text().trim();
  	confirmDelete(divisionId, divisionName);
});
$(document).on('click', '.edit-button', function() {
    var divisionId = $(this).data('division-id');
    openEditModal(divisionId);
});
  
$(document).on('click', '#edit-data-overlay .close', function() {
    closeEditModal();
});