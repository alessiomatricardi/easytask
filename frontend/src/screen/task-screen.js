import React, { useEffect, useState, useCallback } from "react";
import Layout from "../components/layout";
import { useParams } from "react-router-dom";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/Button";
import useFetch from "../utils/useFetch";
import { set } from "date-fns";
import Card from "react-bootstrap/Card";
import { Link } from "react-router-dom";
import Modal from "react-bootstrap/Modal";
import Form from "react-bootstrap/Form";
import instance from "../api";
import { useNavigate } from "react-router-dom";
import { Formik } from "formik";
import * as Yup from "yup";
import { Alert, ListGroup, Badge } from "react-bootstrap";
import { getItemFromLocalStorage } from "../utils/helpers";

const taskValidationSchema = Yup.object({
  description: Yup.string().required("Insert description"),
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

const initialCommentValues = {
  description: "",
};

const commentValidationSchema = Yup.object({
  description: Yup.string().required("Insert comment"),
});

const TaskDetailsScreen = () => {
  const { projectId } = useParams();
  const { taskId } = useParams();
  const navigate = useNavigate();
  const user = getItemFromLocalStorage("user");

  const {
    data: project,
    error: projectError,
    executeFetch: fetchProject,
    isLoading: isProjectLoading,
  } = useFetch(`/api/v1/projects/${projectId}`);
  const getProject = async () => await fetchProject();

  const {
    data: task,
    error: taskError,
    executeFetch: fetchTask,
    isLoading: isTaskLoading,
  } = useFetch(`/api/v1/projects/${projectId}/tasks/${taskId}`);
  const getTask = async () => await fetchTask();

  const {
    data: comments,
    error: commentsError,
    executeFetch: fetchComments,
    isLoading: isCommentsLoading,
  } = useFetch(`/api/v1/projects/${projectId}/tasks/${taskId}/comments`);
  const getComments = async () => await fetchComments();

  const {
    data: members,
    error: membersError,
    executeFetch: fetchMembers,
    isLoading: isMembersLoading,
  } = useFetch(`/api/v1/projects/${projectId}/tasks/${taskId}/assignees`);
  const getMembers = async () => await fetchMembers();

  const {
    data: projectMembers,
    error: projectMembersError,
    executeFetch: fetchProjectMembers,
    isLoading: isProjectMembersLoading,
  } = useFetch(`/api/v1/projects/${projectId}/members`);
  const getProjectMembers = async () => await fetchProjectMembers();

  useEffect(() => {
    getProject();
    getTask();
    getComments();
    getMembers();
    getProjectMembers();
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

  // comment alert
  const [showCommentAlert, setShowCommentAlert] = useState(false);
  const [commentAlertMessage, setCommentAlertMessage] = useState("");
  const [commentAlertVariant, setCommentAlertVariant] = useState("success");

  const getCommentAlert = (message, error = false) => {
    setCommentAlertMessage(message || "Something went bad");
    setCommentAlertVariant(error ? "danger" : "success");
    setShowCommentAlert(true);
  };

  // new comment
  const [showCommentModal, setShowCommentModal] = useState(false);
  const handleCommentClose = () => setShowCommentModal(false);
  const handleCommentShow = () => setShowCommentModal(true);

  const handleCommentSubmit = async (data) => {
    try {
      const response = await instance.post(
        `/api/v1/projects/${projectId}/tasks/${taskId}/comments`,
        data
      );
      handleCommentClose();
      comments.push(response.data);
    } catch (error) {
      // show error message
      getCommentAlert(error.response.data.message, true);
    }
  };

  // new member
  const [showMemberModal, setShowMemberModal] = useState(false);
  const handleMemberClose = () => setShowMemberModal(false);
  const handleMemberShow = () => setShowMemberModal(true);

  const handleMemberSubmit = async (email) => {
    const data = { email };
    try {
      const response = await instance.post(
        `/api/v1/projects/${projectId}/tasks/${taskId}/assignees`,
        data
      );
      handleMemberClose();
      await getMembers();
      await getTask();
    } catch (error) {
      // show error message
      getMemberAlert(error.response.data.message, true);
    }
  };

  // modify task
  const [showTaskModal, setShowTaskModal] = useState(false);
  const handleTaskClose = () => setShowTaskModal(false);
  const handleTaskShow = () => setShowTaskModal(true);

  const handleTaskSubmit = async (data) => {
    const newData = {
      ...data,
      expectedDeliveryDate: data.expectedDeliveryDate + "T00:00:00",
    };
    try {
      const response = await instance.put(
        `/api/v1/projects/${projectId}/tasks/${taskId}`,
        newData
      );
      handleTaskClose();
      await getTask();
    } catch (error) {
      // show error message
      getTaskAlert(error.response.data.message, true);
    }
  };

  // remove member from task

  const handleRemoveMember = async (memberId) => {
    try {
      const response = await instance.delete(
        `/api/v1/projects/${projectId}/tasks/${taskId}/assignees/${memberId}`
      );
      await getMembers();
      await getTask();
    } catch (error) {
      // show error message
      alert(error.response.data.message);
    }
  };

  const handleTaskAction = async (action) => {
    try {
      const response = await instance.put(
        `/api/v1/projects/${projectId}/tasks/${taskId}/${action}`
      );
      await getTask();
    } catch (error) {
      // show error message
      alert(error.response.data.message);
    }
  };

  return (
    <Layout>
      <Container>
        {isTaskLoading ||
        isProjectLoading ||
        !project.projectManager ||
        !project.projectManager.id ? (
          <h3>Is loading...</h3>
        ) : (
          <>
            <Container className="py-2">
              <h1>
                Task <b>{task.description}</b>{" "}
                {members.findIndex((member) => member.id === user.id) !== -1 &&
                  project.status !== "CLOSED" &&
                  (task.status === "PENDING" ? (
                    <Button
                      variant="primary"
                      onClick={() => handleTaskAction("start")}
                    >
                      Start task
                    </Button>
                  ) : task.status === "STARTED" ? (
                    <Button
                      variant="success"
                      onClick={() => handleTaskAction("complete")}
                    >
                      Complete task
                    </Button>
                  ) : (
                    task.status === "COMPLETED" && (
                      <Button
                        variant="warning"
                        onClick={() => handleTaskAction("reopen")}
                      >
                        Reopen task
                      </Button>
                    )
                  ))}
              </h1>
              <Button variant="info" onClick={() => navigate(-1)}>
                Back to the project <b>{project.name}</b>
              </Button>
            </Container>

            <Container>
              <Row>
                <Col className="px-2">
                  <h2>Comments</h2>
                  {project.status === "OPEN" && (
                    <Button
                      className="my-2"
                      variant="primary"
                      onClick={handleCommentShow}
                    >
                      Add comment
                    </Button>
                  )}

                  <Modal show={showCommentModal} onHide={handleCommentClose}>
                    <Modal.Header closeButton>
                      <Modal.Title>Comment the task</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                      <Alert
                        show={showCommentAlert}
                        className="fade"
                        variant={commentAlertVariant}
                        style={{ zIndex: 1 }}
                      >
                        <div className="d-flex justify-content-center">
                          {commentAlertMessage}
                        </div>
                      </Alert>
                      <Formik
                        initialValues={initialCommentValues}
                        validationSchema={commentValidationSchema}
                        onSubmit={handleCommentSubmit}
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
                            <Form.Group
                              className="mb-3"
                              controlId="description"
                            >
                              <Form.Label>Comment</Form.Label>
                              <Form.Control
                                type="text"
                                name="description"
                                value={values.description}
                                placeholder="Comment"
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
                            <Button
                              type="submit"
                              variant="primary"
                              disabled={isSubmitting || !isValid || !dirty}
                            >
                              Add comment
                            </Button>
                          </Form>
                        )}
                      </Formik>
                    </Modal.Body>
                  </Modal>
                  {isCommentsLoading ? (
                    <h3>Loading...</h3>
                  ) : comments.length === 0 ? (
                    <h3>This task has no comments</h3>
                  ) : (
                    comments.map((comment) => {
                      return (
                        <React.Fragment key={comment.id}>
                          <Card className="mb-3">
                            <Card.Header>
                              <b>{comment.employee.email}</b>
                              <br />
                              <div className="align-content-right">
                                on{" "}
                                {new Date(
                                  comment.createdAt
                                ).toLocaleDateString()}{" "}
                                at{" "}
                                {new Date(
                                  comment.createdAt
                                ).toLocaleTimeString()}{" "}
                                wrote
                              </div>
                            </Card.Header>
                            <Card.Body>{comment.description}</Card.Body>
                          </Card>
                        </React.Fragment>
                      );
                    })
                  )}
                </Col>
                <Col className="px-2">
                  <h2>Members</h2>
                  {user.role === "PROJECT_MANAGER" &&
                    user.id === project.projectManager.id &&
                    project.status === "OPEN" &&
                    Array.from(["UNASSIGNED", "PENDING", "STARTED"]).findIndex(
                      (element) => task.status === element
                    ) !== -1 && (
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
                      <Modal.Title>Assign task to a member</Modal.Title>
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
                      <ListGroup>
                        {projectMembers
                          .filter(
                            (projectMember) =>
                              members.findIndex(
                                (taskMember) =>
                                  taskMember.email === projectMember.email
                              ) === -1
                          )
                          .map((projectMember) => {
                            return (
                              <React.Fragment
                                key={`member-${projectMember.id}`}
                              >
                                <ListGroup.Item
                                  action
                                  onClick={() =>
                                    handleMemberSubmit(projectMember.email)
                                  }
                                >
                                  {projectMember.email}
                                </ListGroup.Item>
                              </React.Fragment>
                            );
                          })}
                      </ListGroup>
                    </Modal.Body>
                  </Modal>
                  {isMembersLoading ? (
                    <h3>Loading...</h3>
                  ) : members.length == 0 ? (
                    <h3>This task has no members</h3>
                  ) : (
                    members.map((member) => {
                      return (
                        <React.Fragment key={member.id}>
                          <Card className="mb-3">
                            <Card.Body>
                              {member.email}
                              {user.role === "PROJECT_MANAGER" &&
                                user.id === project.projectManager.id &&
                                project.status === "OPEN" &&
                                Array.from([
                                  "UNASSIGNED",
                                  "PENDING",
                                  "STARTED",
                                ]).findIndex(
                                  (element) => task.status === element
                                ) !== -1 && (
                                  <Button
                                    variant="danger mx-2"
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
                <Col className="px-2">
                  <h2>
                    Details{" "}
                    {user.role === "PROJECT_MANAGER" &&
                      user.id === project.projectManager.id &&
                      project.status === "OPEN" &&
                      task.status !== "COMPLETED" && (
                        <Button variant="danger" onClick={handleTaskShow}>
                          Edit
                        </Button>
                      )}
                  </h2>
                  <Modal show={showTaskModal} onHide={handleTaskClose}>
                    <Modal.Header closeButton>
                      <Modal.Title>Modify task</Modal.Title>
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
                        initialValues={{
                          description: task.description,
                          priority: task.priority,
                          expectedDeliveryDate: String(
                            task.expectedDeliveryDate
                          ).slice(0, 10),
                        }}
                        validationSchema={taskValidationSchema}
                        onSubmit={handleTaskSubmit}
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
                              Apply
                            </Button>
                          </Form>
                        )}
                      </Formik>
                    </Modal.Body>
                  </Modal>
                  <h4>
                    Status <Badge bg="primary">{task.status}</Badge>
                  </h4>
                  <h4>
                    Priority <Badge bg="primary">{task.priority}</Badge>
                  </h4>
                  <h4>
                    Category <Badge bg="primary">{task.category}</Badge>
                  </h4>
                  <h4>
                    Created on {new Date(task.createdAt).toLocaleDateString()}{" "}
                    at {new Date(task.createdAt).toLocaleTimeString()}
                  </h4>
                  <h4>
                    Updated on {new Date(task.updatedAt).toLocaleDateString()}{" "}
                    at {new Date(task.updatedAt).toLocaleTimeString()}
                  </h4>
                  {!task.completedAt && (
                    <h4>
                      To deliver by{" "}
                      {new Date(task.expectedDeliveryDate).toLocaleDateString()}{" "}
                    </h4>
                  )}

                  {task.completedAt && (
                    <h4>
                      Completed on{" "}
                      {new Date(task.completedAt).toLocaleDateString()} at{" "}
                      {new Date(task.completedAt).toLocaleTimeString()}
                    </h4>
                  )}
                </Col>
              </Row>
            </Container>
          </>
        )}
      </Container>
    </Layout>
  );
};

export default TaskDetailsScreen;
