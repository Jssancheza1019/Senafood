/**
 * Lógica para la rotación automática de imágenes en el carrusel (Slider)
 */
document.addEventListener('DOMContentLoaded', () => {
    // Obtiene todos los elementos con la clase 'slider-image'
    const slides = document.querySelectorAll('.slider-image');
    let currentSlide = 0;

    //Mantener funciones showSlide y nextSlide igual
    function showSlide(index) {
        slides.forEach((slide) => {
            slide.classList.remove('active');
        });
        if (slides[index]) {
            slides[index].classList.add('active');
        }
    }
    
    function nextSlide() {
        currentSlide = (currentSlide + 1) % slides.length;
        showSlide(currentSlide);
    }

    //  CORRECCIÓN: Llamar showSlide(0) inmediatamente 
    if (slides.length > 0) {
        // Asegura que la primera diapositiva (0) sea visible INMEDIATAMENTE
        showSlide(currentSlide); 

        if (slides.length > 1) {
            // Inicia la rotación automática después de 5 segundos
            setInterval(nextSlide, 5000); 
        }
    }
});