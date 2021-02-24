function myFunction() {
  if (sessionStorage.getItem("url") == null) {
    alert("No image loaded. Redirecting to Update Emotion page.");
    window.location.href = 'emotion.html';
  } else {
    var fullURL = 'https://ec2-54-167-39-93.compute-1.amazonaws.com/api/empath/playlist?url=' + sessionStorage.getItem("url");
    console.log(fullURL);

    var songList;
    var xhr = new XMLHttpRequest();
    xhr.responseType = 'json';
    // xhr.overrideMimeType("application/json");
    xhr.onreadystatechange = function () {
      if (xhr.readyState == XMLHttpRequest.DONE) {
        if (xhr.status !== 200) {
          alert("No face detected. Redirecting to Update Emotion page.");
          window.location.href = 'emotion.html';
        } else {
          songList = xhr.response;
          console.log(songList);
          getSongFiles(songList);
        }
      }
    }
    xhr.open('GET', fullURL, true);
    xhr.send(null);

    function getSongFiles(songList) {
      for (var i = 0; i < songList.length; i++) {
        var xhr = new XMLHttpRequest();
        xhr.responseType = 'blob';
        xhr.onreadystatechange = function () {
          if (xhr.readyState == XMLHttpRequest.DONE) {
            var blob = new Blob([this.response], { type: 'audio/mp3' });
            console.log(blob);
          }
        }
        xhr.open('GET', 'https://ec2-54-167-39-93.compute-1.amazonaws.com/api/empath/song/' + songList[i].id, true);
        xhr.send(null);
      }
      buildPlayer(songList);
    }
  }
}

function _(query) {
  return document.querySelector(query);
}
function _all(query) {
  return document.querySelectorAll(query);
}

let currentSongIndex = 0;

let player = _(".player"),
  toggleSongList = _(".player .toggle-list");

let main = {
  audio: _(".player .main audio"),
  thumbnail: _(".player .main img"),
  seekbar: _(".player .main input"),
  songname: _(".player .main .details h2"),
  artistname: _(".player .main .details p"),
  prevControl: _(".player .main .controls .prev-control"),
  playPauseControl: _(".player .main .controls .play-pause-control"),
  nextControl: _(".player .main .controls .next-control"),
  favorite: _(".player .main .controls .favorite")
}

toggleSongList.addEventListener("click", function () {
  toggleSongList.classList.toggle("active");
  player.classList.toggle("activeSongList");
});

function buildPlayer(songList) {
  _(".player .player-list .list").innerHTML = (songList.map(function (song, songIndex) {
    return `
		<div class="item" songIndex="${songIndex}">
			<div class="thumbnail">
				<img src="https://ec2-54-167-39-93.compute-1.amazonaws.com/api/empath/song/art/${song.id}">
			</div>
			<div class="details">
				<h2>${song.title}</h2>
				<p>${song.artist}</p>
			</div>
		</div>
	`;
  }).join(""));

  let songListItems = _all(".player .player-list .list .item");
  for (let i = 0; i < songListItems.length; i++) {
    songListItems[i].addEventListener("click", function () {
      currentSongIndex = parseInt(songListItems[i].getAttribute("songIndex"));
      loadSong(currentSongIndex);
      player.classList.remove("activeSongList");
    });
  }

  function loadSong(songIndex) {
    let song = songList[songIndex];
    main.thumbnail.setAttribute("src", "https://ec2-54-167-39-93.compute-1.amazonaws.com/api/empath/song/art/" + song.id);
    //  document.body.style.background = `linear-gradient(#B100FB, #120094, #000000)`;
    //document.body.style.backgroundSize = "cover";	
    main.songname.innerText = song.title;
    main.artistname.innerText = song.artist;
    main.audio.setAttribute("src", "https://ec2-54-167-39-93.compute-1.amazonaws.com/api/empath/song/" + song.id);
    main.seekbar.setAttribute("value", 0);
    main.seekbar.setAttribute("min", 0);
    main.seekbar.setAttribute("max", 0);
    if (song.favorite) {
      main.favorite.classList.add("favorite");
    } else {
      main.favorite.classList.remove("favorite");
    }
    main.audio.addEventListener("canplay", function () {
      main.audio.play();
      if (!main.audio.paused) {
        main.playPauseControl.classList.remove("paused");
      }
      main.seekbar.setAttribute("max", parseInt(main.audio.duration));
      main.audio.onended = function () {
        main.nextControl.click();
      }
    })
  }
  setInterval(function () {
    main.seekbar.value = parseInt(main.audio.currentTime);
  }, 1000);

  main.prevControl.addEventListener("click", function () {
    currentSongIndex--;
    if (currentSongIndex < 0) {
      currentSongIndex = songList.length + currentSongIndex;
    }
    loadSong(currentSongIndex);
  });
  main.nextControl.addEventListener("click", function () {
    currentSongIndex = (currentSongIndex + 1) % songList.length;
    loadSong(currentSongIndex);
  });
  main.playPauseControl.addEventListener("click", function () {
    if (main.audio.paused) {
      main.playPauseControl.classList.remove("paused");
      main.audio.play();
    } else {
      main.playPauseControl.classList.add("paused");
      main.audio.pause();
    }
  });
  main.seekbar.addEventListener("change", function () {
    main.audio.currentTime = main.seekbar.value;
  });
  loadSong(currentSongIndex);
}