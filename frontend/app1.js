/* ANIMACION DE ENTRADA */

window.onload = function(){

    setTimeout(function(){

        document.getElementById("splash").style.display = "none";
        document.getElementById("contenidoLanding").classList.remove("hidden");

    },3000);

};


/* CAROUSEL */

let slides = document.querySelectorAll(".slide");

let index = 0;

function cambiarSlide(){

    slides[index].classList.remove("active");

    index++;

    if(index >= slides.length){
        index = 0;
    }

    slides[index].classList.add("active");

}
/* SCROLL ANIMATION */

function reveal(){

    let reveals = document.querySelectorAll(".reveal");

    for(let i=0;i<reveals.length;i++){

        let windowHeight = window.innerHeight;
        let elementTop = reveals[i].getBoundingClientRect().top;
        let elementVisible = 100;

        if(elementTop < windowHeight - elementVisible){
            reveals[i].classList.add("active");
        }

    }

}

window.addEventListener("scroll", reveal);

setInterval(cambiarSlide,5000);
