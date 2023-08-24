import React, { useState } from "react";
import Layout from "../components/layout";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import instance from "../api";
import { Alert } from "react-bootstrap";
import { Formik } from "formik";
import * as Yup from "yup";

const validationSchema = Yup.object({
  firstName: Yup.string().required("Insert your first name"),
  lastName: Yup.string().required("Insert your last name"),
  email: Yup.string().email("Email is not valid").required("Insert your email"),
  password: Yup.string()
    .min(8, "Password must have at least 8 characters")
    .required("Insert your password"),
  role: Yup.string(),
});

const initialValues = {
  firstName: "",
  lastName: "",
  email: "",
  password: "",
  role: "MEMBER",
};

const RegisterScreen = () => {
  const [isLoading, setLoading] = useState(false);
  const navigate = useNavigate();

  const [showAlert, setShowAlert] = useState(false);
  const [alertMessage, setAlertMessage] = useState("");
  const [alertVariant, setAlertVariant] = useState("success");

  const getAlert = (message, error = false) => {
    setAlertMessage(message || "Something went bad");
    setAlertVariant(error ? "danger" : "success");
    setShowAlert(true);
  };

  const handleSubmit = async (data) => {
    setLoading(true);
    try {
      const response = await instance.post("/api/v1/auth/register", data);
      getAlert("User correctly registered");
    } catch (error) {
      // show error message
      getAlert(error.response.data.message, true);
    }
    setLoading(false);
  };

  return (
    <Layout>
      <Container>
        <Alert
          show={showAlert}
          className="popup fade"
          variant={alertVariant}
          style={{ zIndex: 1 }}
        >
          <div className="d-flex justify-content-center">{alertMessage}</div>
          <div className="d-flex justify-content-center pt-4">
            <Button
              onClick={() => setShowAlert(false)}
              variant={`outline-${alertVariant}`}
            >
              Close
            </Button>
          </div>
        </Alert>

        <Row className="p-5 justify-content-md-center justify-items-md-center">
          <Col md={{ span: 6 }}>
            <h1>Register a new account</h1>
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
                  <Form.Group className="mb-3" controlId="firstName">
                    <Form.Label>First name</Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Enter first name"
                      name="firstName"
                      value={values.firstName}
                      onChange={handleChange}
                      onBlur={handleBlur}
                    />
                    {touched.firstName && errors.firstName ? (
                      <div className="invalid-form">{errors.firstName}</div>
                    ) : (
                      <div className="valid-form"></div>
                    )}
                  </Form.Group>

                  <Form.Group className="mb-3" controlId="lastName">
                    <Form.Label>Last name</Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Enter last name"
                      name="lastName"
                      value={values.lastName}
                      onChange={handleChange}
                      onBlur={handleBlur}
                    />
                    {touched.lastName && errors.lastName ? (
                      <div className="invalid-form">{errors.lastName}</div>
                    ) : (
                      <div className="valid-form"></div>
                    )}
                  </Form.Group>

                  <Form.Group className="mb-3" controlId="email">
                    <Form.Label>Company email address</Form.Label>
                    <Form.Control
                      type="email"
                      placeholder="Enter email"
                      value={values.email}
                      onChange={handleChange}
                      onBlur={handleBlur}
                    />
                    {touched.email && errors.email ? (
                      <div className="invalid-form">{errors.email}</div>
                    ) : (
                      <div className="valid-form"></div>
                    )}
                  </Form.Group>

                  <Form.Group className="mb-3" controlId="password">
                    <Form.Label>Password</Form.Label>
                    <Form.Control
                      type="password"
                      placeholder="Password"
                      value={values.password}
                      onChange={handleChange}
                      onBlur={handleBlur}
                    />
                    {touched.password && errors.password ? (
                      <div className="invalid-form">{errors.password}</div>
                    ) : (
                      <div className="valid-form"></div>
                    )}
                  </Form.Group>
                  <Form.Group className="mb-3" controlId="role">
                    <Form.Label>Role</Form.Label>
                    <Form.Select
                      name="role"
                      aria-label="Role"
                      value={values.role}
                      onChange={handleChange}
                      onBlur={handleBlur}
                    >
                      <option value="MEMBER">Member</option>
                      <option value="PROJECT_MANAGER">Project Manager</option>
                    </Form.Select>
                  </Form.Group>
                  <Button
                    type="submit"
                    variant="primary"
                    disabled={isSubmitting || !isValid || !dirty}
                  >
                    Register
                  </Button>
                </Form>
              )}
            </Formik>
          </Col>
        </Row>
      </Container>
    </Layout>
  );
};

export default RegisterScreen;
