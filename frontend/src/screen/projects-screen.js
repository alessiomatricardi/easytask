import React, { useEffect, useState } from "react";
import { getItemFromLocalStorage } from "../utils/helpers";
import Container from "react-bootstrap/esm/Container";
import Button from "react-bootstrap/esm/Button";
import useFetch from "../utils/useFetch";
import { Link, useNavigate } from "react-router-dom";
import Modal from "react-bootstrap/Modal";
import Form from "react-bootstrap/Form";
import instance from "../api";
import Layout from "../components/layout";
import { Alert, Row, Col, ListGroup } from "react-bootstrap";
import { Formik } from "formik";
import * as Yup from "yup";

const validationSchema = Yup.object({
  name: Yup.string().required("Insert project name"),
});

const initialValues = {
  name: "",
};

const ProjectsScreen = () => {
  const user = getItemFromLocalStorage("user");

  const {
    data: managingProjects,
    executeFetch: fetchManaging,
    error: managingError,
    isLoading: isManagingLoading,
  } = useFetch("/api/v1/projects/managing");
  const getManagingProjects = async () => await fetchManaging();

  const {
    data: workingProjects,
    executeFetch: fetchWorking,
    error: workingError,
    isLoading: isWorkingLoading,
  } = useFetch("/api/v1/projects/working");
  const getWorkingProject = async () => await fetchWorking();

  useEffect(() => {
    getManagingProjects();
    getWorkingProject();
  }, []);

  const navigate = useNavigate();

  const [showAlert, setShowAlert] = useState(false);
  const [alertMessage, setAlertMessage] = useState("");
  const [alertVariant, setAlertVariant] = useState("success");

  const getAlert = (message, error = false) => {
    setAlertMessage(message || "Something went bad");
    setAlertVariant(error ? "danger" : "success");
    setShowAlert(true);
  };

  // new project
  const [showNewProjectModal, setShowNewProjectModal] = useState(false);
  const handleClose = () => setShowNewProjectModal(false);
  const handleShow = () => setShowNewProjectModal(true);

  const handleSubmit = async (data) => {
    try {
      const response = await instance.post("/api/v1/projects", data);
      managingProjects.push(response.data);
      getAlert("Project created correctly");
    } catch (error) {
      // show error message
      getAlert(error.response.data.message, true);
    }
  };

  return (
    <Layout>
      <Container>
        <Row>
          {user.role === "PROJECT_MANAGER" && (
            <Col className="px-2">
              <h1>
                Projects you manage{" "}
                <Button variant="primary" onClick={handleShow}>
                  Create new project
                </Button>
                <Modal show={showNewProjectModal} onHide={handleClose}>
                  <Modal.Header closeButton>
                    <Modal.Title>Create new project</Modal.Title>
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
                          <Form.Group className="mb-3" controlId="name">
                            <Form.Label>Project name</Form.Label>
                            <Form.Control
                              type="text"
                              name="name"
                              value={values.name}
                              placeholder="Project name"
                              onBlur={handleBlur}
                              onChange={handleChange}
                            />
                          </Form.Group>
                          {touched.name && errors.name ? (
                            <div className="invalid-form">{errors.name}</div>
                          ) : (
                            <div className="valid-form"></div>
                          )}
                          <Button
                            type="submit"
                            variant="primary"
                            disabled={isSubmitting || !isValid || !dirty}
                          >
                            Create
                          </Button>
                        </Form>
                      )}
                    </Formik>
                  </Modal.Body>
                </Modal>
              </h1>
              {isManagingLoading ? (
                <h3>Loading data...</h3>
              ) : managingProjects.length > 0 ? (
                <ListGroup>
                  {" "}
                  {managingProjects.map((project) => {
                    return (
                      <React.Fragment key={project.id}>
                        <ListGroup.Item
                          as="li"
                          className="d-flex justify-content-between align-items-start"
                        >
                          <div className="ms-2 me-auto">
                            <div className="fw-bold">{project.name}</div>
                            Status: {project.status}
                          </div>

                          <Link to={`/projects/${project.id}`}>
                            <Button bg="primary">See details</Button>
                          </Link>
                        </ListGroup.Item>
                      </React.Fragment>
                    );
                  })}
                </ListGroup>
              ) : (
                <h3>You don't manage any project</h3>
              )}
            </Col>
          )}
          <Col className="px-2">
            {user.role === "PROJECT_MANAGER" ? (
              <h1>Project you're involved</h1>
            ) : (
              <h1>Your projects</h1>
            )}
            {isWorkingLoading ? (
              <h3>Loading data...</h3>
            ) : workingProjects.length > 0 ? (
              <ListGroup>
                {" "}
                {workingProjects.map((project) => {
                  return (
                    <React.Fragment key={project.id}>
                      <ListGroup.Item
                        as="li"
                        className="d-flex justify-content-between align-items-start"
                      >
                        <div className="ms-2 me-auto">
                          <div className="fw-bold">{project.name}</div>
                          Status: {project.status}
                        </div>

                        <Link to={`/projects/${project.id}`}>
                          <Button bg="primary">See details</Button>
                        </Link>
                      </ListGroup.Item>
                    </React.Fragment>
                  );
                })}
              </ListGroup>
            ) : (
              <h3>You don't work in any project</h3>
            )}
          </Col>
          {user.role === "MEMBER" && <Col></Col>}
        </Row>
      </Container>
    </Layout>
  );
};

export default ProjectsScreen;
