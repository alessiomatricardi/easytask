import HomeScreen from "./screen/home-screen";
import RegisterScreen from "./screen/register-screen";
import ProjectDetailsScreen from "./screen/project-details-screen";
import TaskDetailsScreen from "./screen/task-screen";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginScreen from "./screen/login-screen";
import { getItemFromLocalStorage } from "./utils/helpers";
import { Navigate } from "react-router-dom";
import ProjectsScreen from "./screen/projects-screen";

function App() {
  const user = getItemFromLocalStorage("user");
  return (
    <Router>
      <Routes>
        {/* define all the routes */}
        <Route path="/" element={<HomeScreen />} />
        <Route path="/login" element={<LoginScreen />} />
        <Route path="/register" element={<RegisterScreen />} />
        <Route path="/projects/:projectId" element={<ProjectDetailsScreen />} />
        <Route
          path="/projects/:projectId/tasks/:taskId"
          element={<TaskDetailsScreen />}
        />
      </Routes>
    </Router>
  );
}

export default App;
