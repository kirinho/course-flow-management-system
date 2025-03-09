import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";

const Footer = () => {
  return (
    <>
      <div className="container-fluid bg-dark text-white border-top py-4 px-sm-3 px-md-5" style={{ borderColor: "rgba(256, 256, 256, .1)" }}>
        <div className="row">
          <div className="col-lg-6 text-center text-md-left mb-3 mb-md-0">
            <p className="m-0 text-white">&copy; <a href="/">CourseFlow</a>. All Rights Reserved.</p>
          </div>
          <div className="col-lg-6 text-center text-md-right">
            <ul className="nav d-inline-flex">
              <li className="nav-item"><a className="nav-link text-white py-0" href="#">Privacy</a></li>
              <li className="nav-item"><a className="nav-link text-white py-0" href="#">Terms</a></li>
              <li className="nav-item"><a className="nav-link text-white py-0" href="#">FAQs</a></li>
              <li className="nav-item"><a className="nav-link text-white py-0" href="#">Help</a></li>
            </ul>
          </div>
        </div>
      </div>
    </>
  );
};

export default Footer;
