$(document).ready(function () {
  $("#jqGrid").jqGrid({
    url: 'https://ec2-54-167-39-93.compute-1.amazonaws.com/api/database/songs',
    // we set the changes to be made at client side using predefined word clientArray
    editurl: 'https://ec2-54-167-39-93.compute-1.amazonaws.com/api/database/song',
    datatype: "json",
    colModel: [
      {
        label: 'Id',
        name: 'id',
        hidden: true,
        editable: false,
        editrules: { required: true }
      },
      {
        label: 'File URL',
        name: 'songLocation',
        hidden: true,
        editable: true,
        edittype: "file",
        editrules: { required: true, edithidden: true }
      },
      {
        label: 'Art URL',
        name: 'albumCoverLocation',
        index: 'albumCoverLocation',
        hidden: true,
        editable: true,
        edittype: "file",
        editrules: { required: true, edithidden: true }
      },
      {
        label: 'Title',
        name: 'title',
        width: 240,
        editable: true,
        editrules: { required: true }
      },
      {
        label: 'Artist',
        name: 'artist',
        width: 160,
        editable: true,
        editrules: { required: true }
      },
      {
        label: 'Album',
        name: 'album',
        width: 240,
        editable: true,
        editrules: { required: true }
      },
      {
        label: 'Emotion',
        name: 'emotion',
        width: 160,
        editable: true,
        edittype: "select",
        editoptions: {
          value: "ANGER:Anger;FEAR:Fear;HAPPINESS:Happiness;NEUTRAL:Neutral;SADNESS:Sadness;SURPRISE:Surprise",
          dataEvents: [
            { type: "change", fn: function (e) { changeEmotionSelect($(e.target).val(), e.target); } }
          ]
        },
      },
      {
        label: 'Favorite',
        name: 'favorite',
        width: 50,
        editable: true,
        edittype: "checkbox",
        editoptions: { value: "True:False" }
      }
    ],
    sortname: 'Title',
    sortorder: 'asc',
    loadonce: true,
    viewrecords: true,
    onSelectRow: editRow,
    width: 'auto',
    height: 'auto',
    rowNum: 10,
    altRows: true,
    pager: "#jqGridPager"
  });

  $('#jqGrid').navGrid('#jqGridPager',
    // the buttons to appear on the toolbar of the grid
    { edit: false, add: true, del: false, search: true, refresh: true, view: false, position: "left", cloneToTop: false },
    // options for the Edit Dialog
    {
      editCaption: "Edit Song",
      recreateForm: true,
      //checkOnUpdate : true,
      //checkOnSubmit : true,
      beforeSubmit: function (postdata, form, oper) {
        if (confirm('Are you sure you want to update this row?')) {
          // do something
          return [true, ''];
        } else {
          return [false, 'You can not submit!'];
        }
      },
      closeAfterEdit: true,
      errorTextFormat: function (data) {
        return 'Error: ' + data.responseText
      }
    },
    // options for the Add Dialog
    {
      addCaption: "Add New Song",
      ajaxEditOptions: jsonOptions,
      serializeEditData: createJSON,
      closeAfterAdd: true,
      reloadAfterSubmit: false,
      recreateForm: true,
      onInitializeForm: function (formid) {
        $(formid).attr('method', 'POST');
        $(formid).attr('action', '');
        $(formid).attr('enctype', 'multipart/form-data');
      },
      errorTextFormat: function (data) {
        if (data.responseJSON.status == 409) {
          return 'Error: Song already exists.';
        } else {
          return 'Error: ' + data.responseText
        }
      },
      afterSubmit: function (response, postdata) {
        uploadSongFiles(response, postdata);
        return [true, "", $.parseJSON(response.responseText).id];
      }
    },
    // options for the Delete Dailog
    {
      errorTextFormat: function (data) {
        return 'Error: ' + data.responseText
      }
    });
});
var jsonOptions = {
  type: "POST",
  contentType: "application/json; charset=utf-8",
  dataType: "json"
};
function createJSON(postdata) {
  if (postdata.id === '_empty')
    postdata.id = null; // rest api expects int or null
  return JSON.stringify(postdata)
}

var lastSelection;
function editRow(id) {
  if (id && id !== lastSelection) {
    var grid = $("#jqGrid");
    grid.jqGrid('restoreRow', lastSelection);
    var title = grid.jqGrid('getColProp', 'title');
    var artist = grid.jqGrid('getColProp', 'artist');
    var album = grid.jqGrid('getColProp', 'album');
    var favorite = grid.jqGrid('getColProp', 'favorite');
    title.editable = false;
    artist.editable = false;
    album.editable = false;
    favorite.editable = false;
    grid.jqGrid('editRow', id, { keys: true });
    title.editable = true;
    artist.editable = true;
    album.editable = true;
    favorite.editable = true;
    lastSelection = id;
  }
}

changeEmotionSelect = function (emotionId, emotionElem) {
  var rowID = emotionElem.id;
  if (rowID !== "emotion") {
    var grid = $("#jqGrid");
    var songID = rowID.split("_", 1);
    var id = grid.jqGrid('getColProp', 'id');

    const formData = new FormData();
    formData.append('id', songID);
    formData.append('emotion', emotionId);

    return fetch('https://ec2-54-167-39-93.compute-1.amazonaws.com/api/database/song/' + songID + '/emotion/' + emotionId, {
      method: 'PUT',
      body: formData
    }).then(response => response.json())
  }
}

function uploadSongFiles(response, postdata) {

  var data = $.parseJSON(response.responseText);
  var songFile = $("#songLocation")[0].files[0];
  var artFile = $("#albumCoverLocation")[0].files[0];
  var formData = new FormData();
  formData.append('songFile', songFile);
  formData.append('artFile', artFile);
  var xhr = new XMLHttpRequest();
  xhr.open('POST', 'https://ec2-54-167-39-93.compute-1.amazonaws.com/api/empath/uploadSongFiles/' + data.id);
  xhr.send(formData);

}