import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import api from '@/lib/api';
import { BookOpen, Star, Eye } from 'lucide-react';

interface Book {
  id: number;
  title: string;
  description: string;
  authorName: string;
  genreName: string;
  averageRating: number;
  reviewsCount: number;
  viewsCount: number;
}

export function HomePage() {
  const [books, setBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadBooks();
  }, []);

  const loadBooks = async () => {
    try {
      const response = await api.get('/books?page=0&size=6');
      setBooks(response.data.content || []);
    } catch (error) {
      console.error('Ошибка загрузки книг:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-12">Загрузка...</div>;
  }

  return (
    <div className="space-y-8">
      <section className="text-center py-12 bg-gradient-to-r from-primary/10 to-primary/5 rounded-lg">
        <h1 className="text-4xl font-bold mb-4">Добро пожаловать в BookLoverBox</h1>
        <p className="text-xl text-muted-foreground mb-8">
          Платформа для начинающих авторов и любителей чтения
        </p>
        <div className="flex justify-center gap-4">
          <Link to="/books">
            <Button size="lg">
              <BookOpen className="mr-2 h-5 w-5" />
              Смотреть книги
            </Button>
          </Link>
          <Link to="/register">
            <Button size="lg" variant="outline">
              Стать автором
            </Button>
          </Link>
        </div>
      </section>

      <section>
        <h2 className="text-3xl font-bold mb-6">Популярные книги</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {books.map((book) => (
            <Card key={book.id} className="hover:shadow-lg transition-shadow">
              <CardHeader>
                <CardTitle className="line-clamp-2">{book.title}</CardTitle>
                <CardDescription>{book.authorName}</CardDescription>
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground line-clamp-3 mb-4">
                  {book.description}
                </p>
                <div className="flex items-center justify-between text-sm">
                  <div className="flex items-center gap-4">
                    <span className="flex items-center gap-1">
                      <Star className="h-4 w-4 text-yellow-500" />
                      {book.averageRating.toFixed(1)}
                    </span>
                    <span className="flex items-center gap-1">
                      <Eye className="h-4 w-4" />
                      {book.viewsCount}
                    </span>
                  </div>
                  <Link to={`/books/${book.id}`}>
                    <Button size="sm">Читать</Button>
                  </Link>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </section>
    </div>
  );
}
