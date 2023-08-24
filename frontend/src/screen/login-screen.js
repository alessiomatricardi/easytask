import React, { useState } from "react";
import Layout from "../components/layout";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { useNavigate } from "react-router-dom";
import instance from "../api";
import { getItemFromLocalStorage, setLocalStorageItem } from "../utils/helpers";
import { Formik } from "formik";
import * as Yup from "yup";
import { Alert } from "react-bootstrap";

const validationSchema = Yup.object({
  email: Yup.string().email("Email is not valid").required("Insert your email"),
  password: Yup.string()
    .min(8, "Password must have at least 8 characters")
    .required("Insert your password"),
});

const initialValues = {
  email: getItemFromLocalStorage("email") || "",
  password: "",
};

const LoginScreen = () => {
  const user = getItemFromLocalStorage("user");
  const lastEmail = getItemFromLocalStorage("email");
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
      const response = await instance.post("/api/v1/auth/authenticate", data);
      setLocalStorageItem("user", response.data);
      // set token
      setLocalStorageItem("token", response.data.token);
      // set email field in localstorage
      setLocalStorageItem("email", response.data.email);

      getAlert(
        "Login success. You'll be redirect to home page in few seconds..."
      );

      setTimeout(() => navigate("/"), 2000);
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
              onClick={() => {
                setShowAlert(false);
              }}
              variant={`outline-${alertVariant}`}
            >
              Close
            </Button>
          </div>
        </Alert>

        <Row className="p-5 justify-content-md-center justify-items-md-center">
          <Col md={{ span: 6 }}>
            <h1>Login</h1>
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
                  <Form.Group className="mb-3" controlId="email">
                    <Form.Label>Company email address</Form.Label>
                    <Form.Control
                      type="email"
                      placeholder="Email"
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
                      onBlur={handleBlur}
                      onChange={handleChange}
                    />
                    {touched.password && errors.password ? (
                      <div className="invalid-form">{errors.password}</div>
                    ) : (
                      <div className="valid-form"></div>
                    )}
                  </Form.Group>
                  <Button
                    type="submit"
                    variant="primary"
                    disabled={isSubmitting || !isValid || !dirty}
                  >
                    Login
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

export default LoginScreen;
