import React from 'react';
import { Carousel } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import carousel1 from "../assets/img/carousel-1.jpg";
import carousel2 from "../assets/img/carousel-2.jpg";
import carousel3 from "../assets/img/carousel-3.jpg";

const Home = () => {
    const isAuthenticated = localStorage.getItem('token');

    return (
        <div>
            <Carousel fade>
                <Carousel.Item style={{ minHeight: '300px' }}>
                    <img className="d-block w-100" src={carousel1} alt="Slide 1" style={{ objectFit: 'cover' }} />
                    <Carousel.Caption>
                        <div className="p-5" style={{ maxWidth: '900px', margin: 'auto' }}>
                            <h5 className="text-white text-uppercase mb-3">Best Online Courses</h5>
                            <h1 className="display-3 text-white mb-4">Best Education From Your Home</h1>
                            <a href="/" className="btn btn-primary py-2 px-4 font-weight-semi-bold mt-2">Learn More</a>
                        </div>
                    </Carousel.Caption>
                </Carousel.Item>

                <Carousel.Item style={{ minHeight: '300px' }}>
                    <img className="d-block w-100" src={carousel2} alt="Slide 2" style={{ objectFit: 'cover' }} />
                    <Carousel.Caption>
                        <div className="p-5" style={{ maxWidth: '900px', margin: 'auto' }}>
                            <h5 className="text-white text-uppercase mb-3">Best Online Courses</h5>
                            <h1 className="display-3 text-white mb-4">Best Online Learning Platform</h1>
                            <a href="/" className="btn btn-primary py-2 px-4 font-weight-semi-bold mt-2">Learn More</a>
                        </div>
                    </Carousel.Caption>
                </Carousel.Item>

                <Carousel.Item style={{ minHeight: '300px' }}>
                    <img className="d-block w-100" src={carousel3} alt="Slide 3" style={{ objectFit: 'cover' }} />
                    <Carousel.Caption>
                        <div className="p-5" style={{ maxWidth: '900px', margin: 'auto' }}>
                            <h5 className="text-white text-uppercase mb-3">Best Online Courses</h5>
                            <h1 className="display-3 text-white mb-4">New Way To Learn From Home</h1>
                            <a href="/" className="btn btn-primary py-2 px-4 font-weight-semi-bold mt-2">Learn More</a>
                        </div>
                    </Carousel.Caption>
                </Carousel.Item>
            </Carousel>
        </div>
    );
};

export default Home;
