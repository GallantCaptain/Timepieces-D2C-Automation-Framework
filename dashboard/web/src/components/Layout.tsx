import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export function Layout() {
  const { user, logout } = useAuth();

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">
          <span className="brand-dot" />
          Timepieces&nbsp;<strong>Test Dashboard</strong>
        </div>
        <nav className="nav">
          <NavLink to="/" end>
            Overview
          </NavLink>
          <NavLink to="/runs">History</NavLink>
          <NavLink to="/flows">Run Flows</NavLink>
        </nav>
        <div className="user-box">
          <span className="muted">{user?.displayName ?? user?.email}</span>
          <button className="btn ghost" onClick={logout}>
            Sign out
          </button>
        </div>
      </header>
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
}