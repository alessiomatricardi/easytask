import React, { useState } from "react";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { Link, useNavigate } from "react-router-dom";
import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal";
import { getItemFromLocalStorage, setLocalStorageItem } from "../utils/helpers";
import instance from "../api";
import Form from "react-bootstrap/Form";
import { Formik } from "formik";
import * as Yup from "yup";
import { Alert } from "react-bootstrap";

const validationSchema = Yup.object({
  oldPassword: Yup.string().required("Insert old password"),
  newPassword: Yup.string()
    .min(8, "Password must have at least 8 characters")
    .required("Insert the new password"),
  repeatedNewPassword: Yup.string()
    .min(8, "Password must have at least 8 characters")
    .required("Confirm the new password")
    .oneOf([Yup.ref("newPassword"), null], "Passwords must match"),
});

const initialValues = {
  oldPassword: "",
  newPassword: "",
  repeatedNewPassword: "",
};

const Header = () => {
  const [user, setUser] = useState(getItemFromLocalStorage("user"));

  // modal change password
  const [show, setShow] = useState(false);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  const navigate = useNavigate();

  const [showAlert, setShowAlert] = useState(false);
  const [alertMessage, setAlertMessage] = useState("");
  const [alertVariant, setAlertVariant] = useState("success");

  const getAlert = (message, error = false) => {
    setAlertMessage(message || "Something went bad");
    setAlertVariant(error ? "danger" : "success");
    setShowAlert(true);
  };

  const handleLogout = async () => {
    try {
      // set user data to null
      setLocalStorageItem("user", "");
      setLocalStorageItem("token", "");
      setUser({});
      navigate("/");
    } catch (error) {
      // show error message
      alert(error.response.data.message);
    }
  };

  const handleSubmit = async (data) => {
    try {
      const response = await instance.put(
        "/api/v1/employees/change_password",
        data
      );

      getAlert("Password changed");
    } catch (error) {
      // show error message
      getAlert(error.response.data.message, true);
    }
  };

  return (
    <Navbar bg="light" expand="lg">
      <Container>
        <Navbar.Brand>
          <Link to="/">
            <img
              alt=""
              src="/logo.png"
              width="150px"
              height="100%"
              className="d-inline-block align-top"
            />
          </Link>
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="justify-content-end flex-grow-1 pe-3">
            {user && user.id ? (
              <>
                <Button variant="info" className="mx-1">
                  {user && user.email && (
                    <>
                      {user.email} ({user.role})
                    </>
                  )}
                </Button>
                <Button
                  variant="primary"
                  className=" mx-1"
                  onClick={handleShow}
                >
                  Change password
                </Button>

                <Modal show={show} onHide={handleClose}>
                  <Modal.Header closeButton>
                    <Modal.Title>Change password</Modal.Title>
                  </Modal.Header>
                  <Modal.Body>
                    <Alert
                      show={showAlert}
                      className="fade"
                      variant={alertVariant}
                      style={{ zIndex: 1 }}
                    >
                      <div className="d-flex justify-content-center">
                        {alertMessage}
                      </div>
                    </Alert>

                    <Formik
                      initialValues={initialValues}
                      validationSchema={validationSchema}
                      onSubmit={handleSubmit}
                    >
                      {({
                        values,
                        errors,
                        handleBlur,
                        handleChange,
                        handleSubmit,
                        isSubmitting,
                        isValid,
                        touched,
                        dirty,
                      }) => (
                        <Form onSubmit={handleSubmit}>
                          <Form.Group className="mb-3" controlId="oldPassword">
                            <Form.Label>Old password</Form.Label>
                            <Form.Control
                              type="password"
                              name="oldPassword"
                              value={values.oldPassword}
                              placeholder="Old password"
                              onBlur={handleBlur}
                              onChange={handleChange}
                            />
                            {touched.oldPassword && errors.oldPassword ? (
                              <div className="invalid-form">
                                {errors.oldPassword}
                              </div>
                            ) : (
                              <div className="valid-form"></div>
                            )}
                          </Form.Group>
                          <Form.Group className="mb-3" controlId="newPassword">
                            <Form.Label>New password</Form.Label>
                            <Form.Control
                              type="password"
                              name="newPassword"
                              value={values.newPassword}
                              placeholder="New password"
                              onBlur={handleBlur}
                              onChange={handleChange}
                            />
                            {touched.newPassword && errors.newPassword ? (
                              <div className="invalid-form">
                                {errors.newPassword}
                              </div>
                            ) : (
                              <div className="valid-form"></div>
                            )}
                          </Form.Group>
                          <Form.Group
                            className="mb-3"
                            controlId="repeatedNewPassword"
                          >
                            <Form.Label>Confirm the new password</Form.Label>
                            <Form.Control
                              type="password"
                              name="repeatedNewPassword"
                              value={values.repeatedNewPassword}
                              placeholder="Repeat new password"
                              onBlur={handleBlur}
                              onChange={handleChange}
                            />
                            {touched.repeatedNewPassword &&
                            errors.repeatedNewPassword ? (
                              <div className="invalid-form">
                                {errors.repeatedNewPassword}
                              </div>
                            ) : (
                              <div className="valid-form"></div>
                            )}
                          </Form.Group>
                          <Button
                            type="submit"
                            variant="primary"
                            disabled={isSubmitting || !isValid || !dirty}
                          >
                            Change password
                          </Button>
                        </Form>
                      )}
                    </Formik>
                  </Modal.Body>
                </Modal>
                <Button
                  variant="outline-danger"
                  className=" mx-1"
                  onClick={() => handleLogout()}
                >
                  Logout
                </Button>
              </>
            ) : (
              <>
                <Link to="/register" style={{ marginRight: 20 }}>
                  <Button variant="outline-success">Register</Button>
                </Link>
                <Link to="/login">
                  <Button variant="outline-primary">Login</Button>
                </Link>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;
