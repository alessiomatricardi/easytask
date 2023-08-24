import React, { useState } from "react";
import Layout from "../components/layout";
import LoginBody from "./login-screen";
import { getItemFromLocalStorage, setLocalStorageItem } from "../utils/helpers";
import Container from "react-bootstrap/esm/Container";
import Button from "react-bootstrap/esm/Button";
import useFetch from "../utils/useFetch";
import Card from "react-bootstrap/Card";
import { Link, useNavigate, useNavigation } from "react-router-dom";
import Modal from "react-bootstrap/Modal";
import Form from "react-bootstrap/Form";
import instance from "../api";
import ProjectsScreen from "./projects-screen";

const HomeScreen = () => {
  const user = getItemFromLocalStorage("user");
  const { data: managingProjects, isLoading: isManagingLoading } = useFetch(
    "/api/v1/projects/managing"
  );
  const { data: workingProjects, isLoading: isWorkingLoading } = useFetch(
    "/api/v1/projects/working"
  );

  const navigate = useNavigate();

  // new project
  const [show, setShow] = useState(false);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  const [projectName, setProjectName] = useState("");

  const handleSubmit = async () => {
    const data = {
      name: projectName,
    };
    try {
      const response = await instance.post("/api/v1/projects", data);
      handleClose();
      navigate("/projects/" + response.data.id);
    } catch (error) {
      // show error message
      alert(error.response.data.message);
    }
  };

  return user && user.id ? (
    <ProjectsScreen />
  ) : (
    <Layout>
      <div className="bg d-flex align-items-center"></div>
      <Container className="bgtext">
        <h1 className="home-h1">Plan</h1>
        <h1 className="home-h1">Track</h1>
        <h1 className="home-h1">Build your projects</h1>
        <h1>
          Start from{" "}
          <Link className="link-home" to="/register">
            HERE.
          </Link>
        </h1>
      </Container>
    </Layout>
  );
};

export default HomeScreen;
