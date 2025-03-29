import React from 'react';
import { Carousel } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import carousel1 from "../assets/img/carousel-1.jpg";
import carousel2 from "../assets/img/carousel-2.jpg";
import carousel3 from "../assets/img/carousel-3.jpg";
import carousel4 from "../assets/img/carousel-4.jpg";
import carousel5 from "../assets/img/carousel-5.jpg";
import about from "../assets/img/about.jpg";

const Home = () => {

    return (
        <div>
            <Carousel fade>
                <Carousel.Item style={{ minHeight: '300px', maxHeight: '700px' }}>
                    <img className="d-block w-100" src={carousel1} alt="Slide 1" style={{ objectFit: 'cover' }} />
                    <Carousel.Caption>
                        <div className="p-5" style={{ maxWidth: '900px', margin: 'auto' }}>
                            <h5 className="text-white text-uppercase mb-3">Best Online Courses</h5>
                            <h1 className="display-3 text-white mb-4">Best Education From Your Home</h1>
                            <a href="/courses" className="btn btn-primary py-2 px-4 font-weight-semi-bold mt-2">Learn More</a>
                        </div>
                    </Carousel.Caption>
                </Carousel.Item>

                <Carousel.Item style={{ minHeight: '300px', maxHeight: '700px' }}>
                    <img className="d-block w-100" src={carousel2} alt="Slide 2" style={{ objectFit: 'cover' }} />
                    <Carousel.Caption>
                        <div className="p-5" style={{ maxWidth: '900px', margin: 'auto' }}>
                            <h5 className="text-white text-uppercase mb-3">Best Online Courses</h5>
                            <h1 className="display-3 text-white mb-4">Best Online Learning Platform</h1>
                            <a href="/courses" className="btn btn-primary py-2 px-4 font-weight-semi-bold mt-2">Learn More</a>
                        </div>
                    </Carousel.Caption>
                </Carousel.Item>

                <Carousel.Item style={{ minHeight: '300px', maxHeight: '700px' }}>
                    <img className="d-block w-100" src={carousel3} alt="Slide 3" style={{ objectFit: 'cover' }} />
                    <Carousel.Caption>
                        <div className="p-5" style={{ maxWidth: '900px', margin: 'auto' }}>
                            <h5 className="text-white text-uppercase mb-3">Best Online Courses</h5>
                            <h1 className="display-3 text-white mb-4">New Way To Learn From Home</h1>
                            <a href="/courses" className="btn btn-primary py-2 px-4 font-weight-semi-bold mt-2">Learn More</a>
                        </div>
                    </Carousel.Caption>
                </Carousel.Item>

                <Carousel.Item style={{ minHeight: '300px', maxHeight: '700px' }}>
                    <img className="d-block w-100" src={carousel4} alt="Slide 4" style={{ objectFit: 'cover' }} />
                    <Carousel.Caption>
                        <div className="p-5" style={{ maxWidth: '900px', margin: 'auto' }}>
                            <h5 className="text-white text-uppercase mb-3">Innovative Learning</h5>
                            <h1 className="display-3 text-white mb-4">Discover Knowledge Without Limits</h1>
                            <a href="/courses" className="btn btn-primary py-2 px-4 font-weight-semi-bold mt-2">Learn More</a>
                        </div>
                    </Carousel.Caption>
                </Carousel.Item>

                <Carousel.Item style={{ minHeight: '300px', maxHeight: '700px' }}>
                    <img className="d-block w-100" src={carousel5} alt="Slide 5" style={{ objectFit: 'cover' }} />
                    <Carousel.Caption>
                        <div className="p-5" style={{ maxWidth: '900px', margin: 'auto' }}>
                            <h5 className="text-white text-uppercase mb-3">Shape Your Future</h5>
                            <h1 className="display-3 text-white mb-4">Learn, Grow, and Succeed</h1>
                            <a href="/courses" className="btn btn-primary py-2 px-4 font-weight-semi-bold mt-2">Learn More</a>
                        </div>
                    </Carousel.Caption>
                </Carousel.Item>
            </Carousel>
            <div id="about" className="container py-5">
                <div className="row align-items-center">
                    <div className="col-lg-5">
                        <img
                            className="img-fluid rounded mb-4 mb-lg-0"
                            src={about}
                            alt="About Our Platform"
                            style={{ objectFit: 'cover' }}
                        />
                    </div>
                    <div className="col-lg-7">
                        <div className="text-left mb-4">
                            <h5 className="text-primary text-uppercase mb-3" style={{ letterSpacing: "5px" }}>
                                About Us
                            </h5>
                            <h1>Your Gateway to Knowledge</h1>
                        </div>
                        <p>
                            Welcome to our online learning platform, designed to provide you with an intuitive and flexible way to 
                            manage and take courses. Our platform allows instructors to easily create and administer courses, while 
                            students can register, attend lessons, complete assignments, and track their progress. We focus on giving 
                            you the tools to manage courses, modules, lessons, and assignments efficiently, while also offering features 
                            like grade submission and reporting.
                        </p>
                        <p>
                            Whether you are teaching or learning, our platform is designed to streamline the educational process, 
                            making it more accessible, efficient, and engaging. Start your journey today and take advantage of all the 
                            powerful tools we offer to enhance your learning and teaching experience.
                        </p>
                        <a href="/courses" className="btn btn-primary py-2 px-4 font-weight-semi-bold mt-2">
                            Explore Our Courses
                        </a>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Home;
