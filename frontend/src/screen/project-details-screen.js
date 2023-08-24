import React, { useEffect, useState, useCallback } from "react";
import Layout from "../components/layout";
import { useParams } from "react-router-dom";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";
import useFetch from "../utils/useFetch";
import { Link } from "react-router-dom";
import Modal from "react-bootstrap/Modal";
import Form from "react-bootstrap/Form";
import instance from "../api";
import { useNavigate } from "react-router-dom";
import { Alert } from "react-bootstrap";
import { Formik } from "formik";
import * as Yup from "yup";
import { getItemFromLocalStorage } from "../utils/helpers";
import { ListGroup, Card } from "react-bootstrap";

const initialTaskValues = {
  description: "",
  category: "CHORE",
  priority: "LOW",
  expectedDeliveryDate: new Date().toISOString().slice(0, 10),
};

const taskValidationSchema = Yup.object({
  description: Yup.string().required("Insert description"),
  category: Yup.string()
    .required("Insert category")
    .oneOf(["CHORE", "BUGFIX", "FEATURE"]),
  priority: Yup.string()
    .required("Insert priority")
    .oneOf(["LOW", "MEDIUM", "HIGH"]),
  expectedDeliveryDate: Yup.date()
    .min(
      new Date(Date.now() + 1000 * 60 * 60 * 24).toISOString().slice(0, 10),
      "Insert a valid date"
    )
    .required("Insert date"),
});

const initialMemberValues = {
  email: "",
};

const memberValidationSchema = Yup.object({
  email: Yup.string().required("Insert employee email"),
});

const ProjectDetailsScreen = () => {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const user = getItemFromLocalStorage("user");

  const {
    data: project,
    executeFetch: fetchProject,
    error: projectError,
    isLoading: isProjectLoading,
  } = useFetch(`/api/v1/projects/${projectId}`);
  const getProject = async () => await fetchProject();

  const {
    data: tasks,
    executeFetch: fetchTasks,
    error: taskError,
    isLoading: isTasksLoading,
  } = useFetch(`/api/v1/projects/${projectId}/tasks`);
  const getTasks = async () => await fetchTasks();

  const {
    data: members,
    executeFetch: fetchMembers,
    error: memberError,
    isLoading: isMembersLoading,
  } = useFetch(`/api/v1/projects/${projectId}/members`);
  const getMembers = async () => await fetchMembers();

  useEffect(() => {
    getProject();
    getTasks();
    getMembers();
  }, []);

  // member alert
  const [showMemberAlert, setShowMemberAlert] = useState(false);
  const [memberAlertMessage, setMemberAlertMessage] = useState("");
  const [memberAlertVariant, setMemberAlertVariant] = useState("success");

  const getMemberAlert = (message, error = false) => {
    setMemberAlertMessage(message || "Something went bad");
    setMemberAlertVariant(error ? "danger" : "success");
    setShowMemberAlert(true);
  };

  // task alert
  const [showTaskAlert, setShowTaskAlert] = useState(false);
  const [taskAlertMessage, setTaskAlertMessage] = useState("");
  const [taskAlertVariant, setTaskAlertVariant] = useState("success");

  const getTaskAlert = (message, error = false) => {
    setTaskAlertMessage(message || "Something went bad");
    setTaskAlertVariant(error ? "danger" : "success");
    setShowTaskAlert(true);
  };

  // new task
  const [showTaskModal, setShowTaskModal] = useState(false);
  const handleTaskClose = () => setShowTaskModal(false);
  const handleTaskShow = () => setShowTaskModal(true);

  const handleTaskSubmit = async (data) => {
    const newData = {
      ...data,
      expectedDeliveryDate: data.expectedDeliveryDate + "T00:00:00",
    };
    try {
      const response = await instance.post(
        `/api/v1/projects/${projectId}/tasks`,
        newData
      );
      handleTaskClose();
      navigate(`/projects/${projectId}/tasks/` + response.data.id);
    } catch (error) {
      // show error message
      console.log(error);
      getTaskAlert(error.response.data.message, true);
    }
  };

  const handleProposeTaskSubmit = async (data) => {
    const newData = {
      ...data,
      expectedDeliveryDate: data.expectedDeliveryDate + "T00:00:00",
    };
    try {
      const response = await instance.post(
        `/api/v1/projects/${projectId}/tasks/propose`,
        newData
      );
      await getTasks();
      getTaskAlert("task proposed");
    } catch (error) {
      // show error message
      console.log(error);
      getTaskAlert(error.response.data.message, true);
    }
  };

  const handleAcceptPropose = async (taskId) => {
    try {
      const response = await instance.put(
        `/api/v1/projects/${projectId}/tasks/${taskId}/accept`
      );
      await getTasks();
    } catch (error) {
      // show error message
      alert(error.response.data.message, true);
    }
  };

  const handleRejectPropose = async (taskId) => {
    try {
      const response = await instance.put(
        `/api/v1/projects/${projectId}/tasks/${taskId}/reject`
      );
      await getTasks();
    } catch (error) {
      // show error message
      alert(error.response.data.message, true);
    }
  };

  // new member
  const [showMemberModal, setShowMemberModal] = useState(false);
  const handleMemberClose = () => setShowMemberModal(false);
  const handleMemberShow = () => setShowMemberModal(true);

  const handleMemberSubmit = async (data) => {
    try {
      const response = await instance.post(
        `/api/v1/projects/${projectId}/members`,
        data
      );
      handleMemberClose();
      await fetchMembers();
    } catch (error) {
      // show error message
      getMemberAlert(error.response.data.message, true);
    }
  };

  // remove member

  const handleRemoveMember = async (memberId) => {
    try {
      const response = await instance.delete(
        `/api/v1/projects/${projectId}/members/${memberId}`
      );
      await fetchMembers();
    } catch (error) {
      // show error message
      alert(error.response.data.message);
    }
  };

  // close project
  const handleCloseProject = async () => {
    try {
      const response = await instance.put(
        `/api/v1/projects/${projectId}/close`
      );
      await getProject();
    } catch (error) {
      // show error message
      alert(error.response.data.message);
    }
  };

  return (
    <Layout>
      <Container>
        {isProjectLoading ||
        !project.projectManager ||
        !project.projectManager.id ? (
          <h3>Is loading...</h3>
        ) : (
          <>
            <Container className="py-2">
              <h1>
                Project <b>{project.name}</b>{" "}
                {user.role === "PROJECT_MANAGER" &&
                  user.id === project.projectManager.id &&
                  project.status !== "CLOSED" && (
                    <Button
                      variant="danger"
                      onClick={() => handleCloseProject()}
                    >
                      Close project
                    </Button>
                  )}
              </h1>
            </Container>

            <Container>
              <Row>
                <Col className="px-2">
                  <h2>Tasks</h2>
                  {project.status === "OPEN" && (
                    <Button
                      className="my-2"
                      variant="primary"
                      onClick={handleTaskShow}
                    >
                      {user.role === "PROJECT_MANAGER" &&
                      user.id === project.projectManager.id
                        ? "Add task"
                        : "Propose task"}
                    </Button>
                  )}
                  <Modal show={showTaskModal} onHide={handleTaskClose}>
                    <Modal.Header closeButton>
                      <Modal.Title>
                        {user.role === "PROJECT_MANAGER" &&
                        user.id === project.projectManager.id
                          ? `Create new task for ${project.name}`
                          : `Propose new task for ${project.name}`}
                      </Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                      <Alert
                        show={showTaskAlert}
                        className="fade"
                        variant={taskAlertVariant}
                        style={{ zIndex: 1 }}
                      >
                        <div className="d-flex justify-content-center">
                          {taskAlertMessage}
                        </div>
                      </Alert>
                      <Formik
                        initialValues={initialTaskValues}
                        validationSchema={taskValidationSchema}
                        onSubmit={
                          user.role === "PROJECT_MANAGER" &&
                          user.id === project.projectManager.id
                            ? handleTaskSubmit
                            : handleProposeTaskSubmit
                        }
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
                            <Form.Group
                              className="mb-3"
                              controlId="description"
                            >
                              <Form.Label>Task description</Form.Label>
                              <Form.Control
                                type="text"
                                name="description"
                                value={values.description}
                                placeholder="Task description"
                                onChange={handleChange}
                                onBlur={handleBlur}
                              />
                              {touched.description && errors.description ? (
                                <div className="invalid-form">
                                  {errors.description}
                                </div>
                              ) : (
                                <div className="valid-form"></div>
                              )}
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="category">
                              <Form.Label>Task category</Form.Label>
                              <Form.Select
                                name="category"
                                onChange={handleChange}
                                onBlur={handleBlur}
                                value={values.category}
                              >
                                <option value="CHORE">Chore</option>
                                <option value="BUGFIX">Bug fix</option>
                                <option value="FEATURE">Feature</option>
                              </Form.Select>
                            </Form.Group>
                            <Form.Group className="mb-3" controlId="priority">
                              <Form.Label>Task Priority</Form.Label>
                              <Form.Select
                                name="priority"
                                onChange={handleChange}
                                onBlur={handleBlur}
                                value={values.priority}
                              >
                                <option value="LOW">Low</option>
                                <option value="MEDIUM">Medium</option>
                                <option value="HIGH">High</option>
                              </Form.Select>
                            </Form.Group>
                            <Form.Group
                              className="mb-3"
                              controlId="expectedDeliveryDate"
                            >
                              <Form.Label>Expected delivery date</Form.Label>
                              <Form.Control
                                type="date"
                                name="expectedDeliveryDate"
                                value={values.expectedDeliveryDate}
                                placeholder="Task delivery date"
                                onChange={handleChange}
                                onBlur={handleBlur}
                              />
                              {touched.expectedDeliveryDate &&
                              errors.expectedDeliveryDate ? (
                                <div className="invalid-form">
                                  {errors.expectedDeliveryDate}
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
                              {user.role === "PROJECT_MANAGER" &&
                              user.id === project.projectManager.id
                                ? `Create task`
                                : `Propose task`}
                            </Button>
                          </Form>
                        )}
                      </Formik>
                    </Modal.Body>
                  </Modal>
                  {isTasksLoading ? (
                    <h3>Loading...</h3>
                  ) : tasks.length === 0 ? (
                    <h3>There aren't tasks in this project</h3>
                  ) : (
                    <ListGroup>
                      {" "}
                      {tasks
                        .filter(
                          (task) =>
                            task.status !== "PROPOSED" &&
                            task.status !== "REJECTED_PROPOSE"
                        )
                        .map((task) => {
                          return (
                            <React.Fragment key={task.id}>
                              <ListGroup.Item
                                as="li"
                                className="d-flex justify-content-between align-items-start"
                              >
                                <div className="ms-2 me-auto">
                                  <div className="fw-bold">
                                    {task.description}
                                  </div>
                                  Status: {task.status}
                                </div>

                                <Link
                                  to={`/projects/${project.id}/tasks/${task.id}`}
                                >
                                  <Button bg="primary">See details</Button>
                                </Link>
                              </ListGroup.Item>
                            </React.Fragment>
                          );
                        })}
                    </ListGroup>
                  )}
                </Col>
                <Col className="px-2">
                  <h2>Members</h2>
                  {user.role === "PROJECT_MANAGER" &&
                    user.id === project.projectManager.id &&
                    project.status === "OPEN" && (
                      <Button
                        className="my-2"
                        variant="primary"
                        onClick={handleMemberShow}
                      >
                        Add member
                      </Button>
                    )}
                  <Modal show={showMemberModal} onHide={handleMemberClose}>
                    <Modal.Header closeButton>
                      <Modal.Title>
                        Add new member to {project.name}
                      </Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                      <Alert
                        show={showMemberAlert}
                        className="fade"
                        variant={memberAlertVariant}
                        style={{ zIndex: 1 }}
                      >
                        <div className="d-flex justify-content-center">
                          {memberAlertMessage}
                        </div>
                      </Alert>
                      <Formik
                        initialValues={initialMemberValues}
                        validationSchema={memberValidationSchema}
                        onSubmit={handleMemberSubmit}
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
                          <Form onClick={handleSubmit}>
                            <Form.Group className="mb-3" controlId="email">
                              <Form.Label>Employee's email</Form.Label>
                              <Form.Control
                                type="email"
                                name="email"
                                value={values.email}
                                placeholder="Member email"
                                onChange={handleChange}
                                onBlur={handleBlur}
                              />
                              {touched.email && errors.email ? (
                                <div className="invalid-form">
                                  {errors.email}
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
                              Add member
                            </Button>
                          </Form>
                        )}
                      </Formik>
                    </Modal.Body>
                  </Modal>
                  {isMembersLoading ? (
                    <h3>Loading...</h3>
                  ) : members.length === 0 ? (
                    <h3>There aren't members in this project</h3>
                  ) : (
                    members.map((member) => {
                      return (
                        <React.Fragment key={member.id}>
                          <Card className="mb-3">
                            <Card.Body>
                              {member.firstName +
                                " " +
                                member.lastName +
                                " (" +
                                member.email +
                                ") "}
                              {user.role === "PROJECT_MANAGER" &&
                                user.id === project.projectManager.id &&
                                project.status === "OPEN" && (
                                  <Button
                                    variant="danger"
                                    onClick={() =>
                                      handleRemoveMember(member.id)
                                    }
                                  >
                                    Remove
                                  </Button>
                                )}
                            </Card.Body>
                          </Card>
                        </React.Fragment>
                      );
                    })
                  )}
                </Col>
                {isProjectLoading ? (
                  <h3>Loading...</h3>
                ) : (
                  <Col className="px-2">
                    <h2>Details</h2>
                    <h4>Status {project.status}</h4>
                    <h4>
                      Created on{" "}
                      {new Date(project.createdAt).toLocaleDateString()} at{" "}
                      {new Date(project.createdAt).toLocaleTimeString()}
                    </h4>
                    <h4>
                      Updated on{" "}
                      {new Date(project.updatedAt).toLocaleDateString()} at{" "}
                      {new Date(project.updatedAt).toLocaleTimeString()}
                    </h4>
                    {project.closedAt && (
                      <h4>
                        Closed on{" "}
                        {new Date(project.closedAt).toLocaleDateString()} at{" "}
                        {new Date(project.closedAt).toLocaleTimeString()}
                      </h4>
                    )}
                    <h2>Proposed tasks</h2>
                    {isTasksLoading ? (
                      <h3>Loading...</h3>
                    ) : tasks.filter((task) => task.status === "PROPOSED")
                        .length === 0 ? (
                      <h3>There aren't proposed tasks</h3>
                    ) : (
                      tasks
                        .filter((task) => task.status === "PROPOSED")
                        .map((task) => {
                          return (
                            <React.Fragment key={task.id}>
                              <Card className="mb-3">
                                <Card.Header>
                                  <b>{task.description}</b>
                                </Card.Header>
                                <Card.Body>
                                  <Link
                                    to={`/projects/${project.id}/tasks/${task.id}`}
                                  >
                                    <Button variant="primary">
                                      See details
                                    </Button>
                                  </Link>
                                  <br />
                                  {user.role === "PROJECT_MANAGER" &&
                                    user.id === project.projectManager.id &&
                                    project.status === "OPEN" && (
                                      <Button
                                        className="my-2"
                                        variant="success"
                                        onClick={() =>
                                          handleAcceptPropose(task.id)
                                        }
                                      >
                                        Accept
                                      </Button>
                                    )}
                                  {user.role === "PROJECT_MANAGER" &&
                                    user.id === project.projectManager.id &&
                                    project.status === "OPEN" && (
                                      <Button
                                        className="my-2 mx-2"
                                        variant="danger"
                                        onClick={() =>
                                          handleRejectPropose(task.id)
                                        }
                                      >
                                        Reject
                                      </Button>
                                    )}
                                </Card.Body>
                              </Card>
                            </React.Fragment>
                          );
                        })
                    )}
                  </Col>
                )}
              </Row>
            </Container>
          </>
        )}
      </Container>
    </Layout>
  );
};

export default ProjectDetailsScreen;
