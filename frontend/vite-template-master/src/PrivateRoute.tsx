import React from "react";
import { Navigate } from "react-router-dom";

export function PrivateRoute({ children }: { children: React.ReactNode }) {
  const isAuthenticated = localStorage.getItem("jwtToken");

  if(!isAuthenticated){
    return <Navigate to="/" />
  }
  return children;
}