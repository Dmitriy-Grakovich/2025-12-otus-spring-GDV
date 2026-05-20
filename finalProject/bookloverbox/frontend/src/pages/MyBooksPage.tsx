import { useEffect, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import api from '@/lib/api';
import { Plus } from 'lucide-react';

interface Book {
  id: number;
  title: string;
  description: string;
  status: string;
  viewsCount: number;
  reviewsCount: number;
}

export function MyBooksPage() {
  const [books, setBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    genreId: '',
  });

  useEffect(() => {
    loadBooks();
  }, []);

  const loadBooks = async () => {
    try {
      const response = await api.get('/books/my');
      setBooks(response.data.content || []);
    } catch (error) {
      console.error('Ошибка загрузки книг:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/books', formData);
      setFormData({ title: '', description: '', genreId: '' });
      setShowForm(false);
      loadBooks();
    } catch (error) {
      console.error('Ошибка создания книги:', error);
    }
  };

  const handleSubmitToModeration = async (bookId: number) => {
    try {
      await api.post(`/books/${bookId}/moderation`);
      loadBooks();
    } catch (error) {
      console.error('Ошибка отправки на модерацию:', error);
    }
  };

  if (loading) {
    return <div className="text-center py-12">Загрузка...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">Мои книги</h1>
        <Button onClick={() => setShowForm(!showForm)}>
          <Plus className="h-4 w-4 mr-2" />
          Добавить книгу
        </Button>
      </div>

      {showForm && (
        <Card>
          <CardHeader>
            <CardTitle>Новая книга</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <label className="text-sm font-medium">Название</label>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  className="w-full px-3 py-2 border rounded-md"
                  required
                />
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium">Описание</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full px-3 py-2 border rounded-md"
                  rows={4}
                  required
                />
              </div>

              <div className="flex gap-2">
                <Button type="submit">Создать</Button>
                <Button type="button" variant="outline" onClick={() => setShowForm(false)}>
                  Отмена
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {books.map((book) => (
          <Card key={book.id}>
            <CardHeader>
              <CardTitle className="line-clamp-2">{book.title}</CardTitle>
              <CardDescription>
                <span className={`px-2 py-1 rounded text-xs ${
                  book.status === 'PUBLISHED' ? 'bg-green-100 text-green-800' :
                  book.status === 'MODERATION' ? 'bg-yellow-100 text-yellow-800' :
                  book.status === 'REJECTED' ? 'bg-red-100 text-red-800' :
                  'bg-gray-100 text-gray-800'
                }`}>
                  {book.status}
                </span>
              </CardDescription>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-muted-foreground line-clamp-3 mb-4">
                {book.description}
              </p>
              <div className="flex items-center justify-between text-sm mb-4">
                <span>Просмотры: {book.viewsCount}</span>
                <span>Отзывы: {book.reviewsCount}</span>
              </div>
              {book.status === 'DRAFT' && (
                <Button 
                  size="sm" 
                  className="w-full"
                  onClick={() => handleSubmitToModeration(book.id)}
                >
                  Отправить на модерацию
                </Button>
              )}
            </CardContent>
          </Card>
        ))}
      </div>

      {books.length === 0 && (
        <div className="text-center py-12 text-muted-foreground">
          У вас пока нет книг. Создайте свою первую книгу!
        </div>
      )}
    </div>
  );
}
