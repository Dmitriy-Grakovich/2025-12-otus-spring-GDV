import { Link, useNavigate } from 'react-router-dom';
import { Button } from './ui/button';
import { BookOpen, LogOut, User } from 'lucide-react';

export function Header() {
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const isAuthenticated = !!token;

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <header className="border-b">
      <div className="container mx-auto px-4 py-4 flex items-center justify-between">
        <Link to="/" className="flex items-center space-x-2">
          <BookOpen className="h-6 w-6 text-primary" />
          <span className="text-xl font-bold">BookLoverBox</span>
        </Link>

        <nav className="flex items-center space-x-4">
          <Link to="/books">
            <Button variant="ghost">Книги</Button>
          </Link>
          
          {isAuthenticated ? (
            <>
              <Link to="/my-books">
                <Button variant="ghost">Мои книги</Button>
              </Link>
              <Link to="/admin">
                <Button variant="ghost">Админ</Button>
              </Link>
              <Button variant="ghost" onClick={handleLogout}>
                <LogOut className="h-4 w-4 mr-2" />
                Выход
              </Button>
            </>
          ) : (
            <>
              <Link to="/login">
                <Button variant="ghost">Вход</Button>
              </Link>
              <Link to="/register">
                <Button>Регистрация</Button>
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
